package com.ms.stagerschemes.repository;

import com.ms.stagerschemes.model.Scheme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchemeRepository extends JpaRepository<Scheme, Long> {}
