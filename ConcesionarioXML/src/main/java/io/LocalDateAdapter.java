package io;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {
    @Override
    public LocalDate unmarshal(String v) throws Exception {
        return (v != null && !v.isBlank()) ? LocalDate.parse(v) : null;
    }
    @Override
    public String marshal(LocalDate v) throws Exception {
        return (v != null) ? v.toString() : null; // Formato YYYY-MM-DD
    }
}