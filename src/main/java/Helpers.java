import java.util.ArrayList;
import java.util.Map;

public class Helpers {
    // funkcja zwracająca losowy kolor
    public static String getColor(int red, int green, int blue){
        String hexRed;
        String hexGreen;
        String hexBlue;
        if(red < 16)
            hexRed = "0"+Integer.toHexString(red);
        else
            hexRed = Integer.toHexString(red);
        if(green < 16)
            hexGreen = "0"+Integer.toHexString(green);
        else
            hexGreen = Integer.toHexString(green);
        if(blue < 16)
            hexBlue = "0"+Integer.toHexString(blue);
        else
            hexBlue = Integer.toHexString(blue);

        return "#" + hexRed+hexGreen+hexBlue;
    }

    // funkcja zwracająca tablicę stawek VAT
    public static ArrayList<Integer> getVATrates() {
        ArrayList<Integer> VATrates = new ArrayList<>();
        VATrates.add(0);
        VATrates.add(7);
        VATrates.add(22);
        return VATrates;
    }

    // funkcja zwracająca losowo wybraną stawkę z tablicy powyższej
    public static int getRandomVATrate(){
        int randomIndex =(int) Math.floor(Math.random()* getVATrates().size());
        return getVATrates().get(randomIndex);
    }

    // funkcja zwracająca losowo wygenerowaną cenę samochodu
    public static int getRandomCarPrice(){
        return (int) Math.floor(Math.random()* 100000);
    }

    // funkcja zwracająca losową datę
    public static CustomDate getRandomDate(){
        int day, month, year;
        month = (int) Math.ceil(Math.random()* 12);
        if (month == 2)
            day = (int) Math.ceil(Math.random()*28);
        else if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12)
            day = (int) Math.ceil(Math.random()* 31);
        else
            day = (int) Math.ceil(Math.random()*30);
        year = (int) Math.floor(Math.random()* 1022 +1000);
        return new CustomDate(day, month, year);
    }

    // funkcja zwracająca tablicę przechowującę nazwy marek samochodów
    public static ArrayList<String> addModelNames(){
        ArrayList<String> models = new ArrayList<>();
        models.add("opel");
        models.add("bmw");
        models.add("citroen");
        models.add("fiat");
        return models;
    }

    // funkcja zwraca tablicę przechowujacą lokalizację poduszej powietrznych
    public static ArrayList<String> addAirbagsLocalizations(){
        ArrayList<String> airbags = new ArrayList<>();
        airbags.add("kierowca");
        airbags.add("pasazer");
        airbags.add("kanapa");
        airbags.add("boczne");
        return airbags;
    }

    public static double getPriceWithVat(int vat, int price){
        double addon = price * (vat / 100.0);
        return price + addon;
    }
}
