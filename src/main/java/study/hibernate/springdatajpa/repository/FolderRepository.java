package study.hibernate.springdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import study.hibernate.entity.Folder;

public interface FolderRepository extends JpaRepository<Folder, Integer> {

}
