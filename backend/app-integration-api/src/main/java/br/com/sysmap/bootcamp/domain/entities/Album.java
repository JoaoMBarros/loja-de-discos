package br.com.sysmap.bootcamp.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "ALBUM")
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false)
    private Long id;

    @Column(name = "NAME", nullable = false, length = 150)
    private String name;

    @Column(name = "ID_SPOTIFY", nullable = false, length = 100)
    private String idSpotify;

    @Column(name = "ARTIST_NAME", nullable = false, length = 150)
    private String artistName;

    @Column(name = "IMAGE_URL", nullable = false, length = 150)
    private String imageUrl;

    @Column(name = "`VALUE`", nullable = false)
    private BigDecimal value;

    @ManyToOne
    @JoinColumn(name = "ID_USER")
    private Users users;

    @Column(name = "SALE_STATUS", nullable = false, length = 50)
    private String saleStatus;

}