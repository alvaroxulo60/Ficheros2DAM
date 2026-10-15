package models;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "catalogoTienda")
public class CatalogoTienda {
    private String nombreTienda;
    private List<Videojuego> listaJuegos = new ArrayList<>();
    private String claveAdmin; // Atributo confidencial

    public CatalogoTienda() {
    }

    public CatalogoTienda(String nombreTienda, String claveAdmin) {
        this.nombreTienda = nombreTienda;
        this.claveAdmin = claveAdmin;
    }

    @XmlAttribute
    public String getNombreTienda() {
        return nombreTienda;
    }

    public void setNombreTienda(String nombreTienda) {
        this.nombreTienda = nombreTienda;
    }

    @XmlElementWrapper(name = "videojuegos")
    @XmlElement(name = "videojuego")
    public List<Videojuego> getListaJuegos() {
        return listaJuegos;
    }

    public void setListaJuegos(List<Videojuego> listaJuegos) {
        this.listaJuegos = listaJuegos;
    }

    @XmlTransient
    public String getClaveAdmin() {
        return claveAdmin;
    }

    public void setClaveAdmin(String claveAdmin) {
        this.claveAdmin = claveAdmin;
    }
}