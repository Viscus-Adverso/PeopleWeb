package com.company;

import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        ArrayList<Person> allPeople = new ArrayList<>();


        File f = new File("people.csv");
        Scanner fileScanner = new Scanner(f);
        fileScanner.nextLine();
        while (fileScanner.hasNext()) {
            String line = fileScanner.nextLine();
            String[] columns = line.split("\\,");
            int id = Integer.valueOf(columns[0]);
            String firstName = columns[1];
            String lastName = columns[2];
            String email = columns[3];
            String country = columns[4];
            String ipAddress = columns[5];
            Person p = new Person(id, firstName, lastName, email, country, ipAddress);
            allPeople.add(p);
        }

        Spark.get(
                "/",
                (request, response) -> {
                    String offSet = request.queryParams("offSet");
                    int offSetNum = 0;
                    if (offSet != null){
                        offSetNum = Integer.valueOf(offSet);
                    }
                    List<Person> subPeople = allPeople.subList(offSetNum, 20 + offSetNum);
                    HashMap m = new HashMap();
                    m.put("next", 20+offSetNum);
                    m.put("previous", offSetNum-20);
                    m.put("h", subPeople);
                    m.put("shownext", allPeople.size()-offSetNum>20);
                    m.put("showprevious", offSetNum!=0);
                    return new ModelAndView(m, "home.html");
                },
                new MustacheTemplateEngine()

        );

        Spark.get(
                "/person",
                (request, response) -> {
                    String id = request.queryParams("id");
                    int idNum = 0;
                    if (id != null){
                        idNum = Integer.valueOf(id);
                    }
                    Person p = allPeople.get(idNum-1);

                    return new ModelAndView(p, "person.html");
                },
                new MustacheTemplateEngine()
        );

    }
}
