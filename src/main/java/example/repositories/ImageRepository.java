package example.repositories;

import example.model.Image;
import jakarta.persistence.NamedQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    @Query(value = "SELECT img FROM Image img JOIN img.objects imgobjs WHERE imgobjs IN (:objectNames)")
    List<Image> findContainingObject(List<String> objectNames);
}
