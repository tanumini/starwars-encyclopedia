package com.starwars.repository;

import com.starwars.model.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {

@Query("SELECT DISTINCT f FROM Film f WHERE f.title = :title")
List<Film> findDistinctByTitle(String title);



}
