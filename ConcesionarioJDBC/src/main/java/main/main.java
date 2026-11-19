package main;

import exceptions.BBDDException;
import services.SQL;


public class main {
    static void main(String[] args) {
        try {
            SQL base = new SQL();

            base.createDatabase();
            base.createDDBBLite();
        } catch (BBDDException e) {
            System.out.println("Error: "+e.getMessage());
        }
    }
}
