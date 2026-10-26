package models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import jakarta.xml.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
public class Coche {


    @XmlAttribute
    private int id;

    @XmlElement(name = "matricula-coche")
    private String matricula;
    private String marca;
    private String modelo;

    @XmlElementWrapper(name = "equipamiento")
    @XmlElement(name = "extra")
    private List<Equipamiento> equipamiento;

    @XmlTransient
    @Expose(serialize = false)
    private static int contador = 1;

    public Coche() {
        id = contador++;
        equipamiento = new ArrayList<>();
    }

    public Coche(String matricula, String marca, String modelo) {
        id = contador++;
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        equipamiento = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public String getMatricula() {
        return matricula;
    }

    private  void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public List<Equipamiento> getEquipamiento() {
        return equipamiento;
    }

    public void setEquipamiento(List<Equipamiento> equipamiento) {
        this.equipamiento = equipamiento;
    }

    @Override
    public String toString() {
        return "Coche{" +
                "id=" + id +
                ", matricula='" + matricula + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", equipamiento=" + equipamiento +
                '}';
    }
}
