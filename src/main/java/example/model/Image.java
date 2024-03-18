package example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.List;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@Entity
@Table(name="image")
public class Image {

    @Id
    @GeneratedValue
    Long id;

    String url;

    String label;

    Boolean objectDetection;

    @ElementCollection(targetClass=String.class, fetch= FetchType.EAGER)
    @CollectionTable(name = "image_object", joinColumns = @JoinColumn(name = "image_id"))
    @Column(name = "object")
    //@Fetch(FetchMode.JOIN)
    List<String> objects;

    @Transient
    byte[] content;
}
