package com.filmarchive.controller;

import com.filmarchive.model.Film;
import com.filmarchive.service.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/films")
public class FilmController {

    @Autowired
    private FilmService filmService;

    // **1️⃣ Afficher tous les films et gérer l'édition**
    @GetMapping
    public String getAllFilms(@RequestParam(value = "edit", required = false) Long editFilmId, Model model) {
        List<Film> films = filmService.getAllFilms();
        model.addAttribute("films", films);

        if (editFilmId != null) {
            Optional<Film> selectedFilm = filmService.getFilmById(editFilmId);
            selectedFilm.ifPresent(film -> model.addAttribute("selectedFilm", film));
        }

        return "index";
    }

    // **2️⃣ Ajouter un film**
    @PostMapping("/add")
    public String addFilm(@RequestParam String titre,
                          @RequestParam String realisateur,
                          @RequestParam int annee,
                          @RequestParam String genre) {
        Film film = new Film();
        film.setTitre(titre);
        film.setRealisateur(realisateur);
        film.setAnnee(annee);
        film.setGenre(genre);
        filmService.saveFilm(film);
        return "redirect:/films";
    }

    // **3️⃣ Modifier un film**
    @PostMapping("/update/{id}")
    public String updateFilm(@PathVariable Long id,
                             @RequestParam String titre,
                             @RequestParam String realisateur,
                             @RequestParam int annee,
                             @RequestParam String genre) {
        Optional<Film> filmOptional = filmService.getFilmById(id);
        if (filmOptional.isPresent()) {
            Film film = filmOptional.get();
            film.setTitre(titre);
            film.setRealisateur(realisateur);
            film.setAnnee(annee);
            film.setGenre(genre);
            filmService.saveFilm(film);
        }
        return "redirect:/films";
    }

    // **4️⃣ Supprimer un film**
    @PostMapping("/delete/{id}")
    public String deleteFilm(@PathVariable Long id) {
        if (filmService.getFilmById(id).isPresent()) {
            filmService.deleteFilm(id);
        }
        return "redirect:/films";
    }

    // ======================== API REST (JSON) ========================

    // **🔹 API : Récupérer tous les films (JSON)**
    @GetMapping("/api")
    @ResponseBody
    public List<Film> getAllFilmsApi() {
        return filmService.getAllFilms();
    }

    // **🔹 API : Récupérer un film par ID (JSON)**
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Film> getFilmById(@PathVariable Long id) {
        Optional<Film> film = filmService.getFilmById(id);
        return film.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // **🔹 API : Ajouter un film (JSON)**
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<Film> createFilm(@RequestBody Film film) {
        Film savedFilm = filmService.saveFilm(film);
        return ResponseEntity.ok(savedFilm);
    }

    // **🔹 API : Modifier un film (JSON)**
    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Film> updateFilmApi(@PathVariable Long id, @RequestBody Film updatedFilm) {
        Optional<Film> filmOptional = filmService.getFilmById(id);
        if (filmOptional.isPresent()) {
            Film film = filmOptional.get();
            film.setTitre(updatedFilm.getTitre());
            film.setRealisateur(updatedFilm.getRealisateur());
            film.setAnnee(updatedFilm.getAnnee());
            film.setGenre(updatedFilm.getGenre());
            filmService.saveFilm(film);
            return ResponseEntity.ok(film);
        }
        return ResponseEntity.notFound().build();
    }

    // **🔹 API : Supprimer un film (JSON)**
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteFilmApi(@PathVariable Long id) {
        if (!filmService.getFilmById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        filmService.deleteFilm(id);
        return ResponseEntity.noContent().build();
    }
}
