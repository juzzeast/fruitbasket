package org.yuri.glushko.assesment.fruitbasket;

import org.apache.commons.cli.*;
import org.yuri.glushko.assesment.fruitbasket.dao.FruitBasketDAO;
import org.yuri.glushko.assesment.fruitbasket.exceptions.DatabaseException;
import org.yuri.glushko.assesment.fruitbasket.exceptions.WrongDataFormatException;
import org.yuri.glushko.assesment.fruitbasket.service.FruitBasketDataIngester;
import org.yuri.glushko.assesment.fruitbasket.service.FruitBasketReportBuilder;

public class FruitBasketApp {

    public static void main(String[] args) {
        // Parsing the options
        Options options = new Options();
        Option input = new Option("i", "input", true,
                "input file with fruit basket data in csv");
        input.setRequired(false);
        options.addOption(input);
        Option output = new Option("h", "help", false, "usage");
        output.setRequired(false);
        options.addOption(output);
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        String csvFile = null;
        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("input")) {
                csvFile = cmd.getOptionValue("input");
            } else {
                formatter.printHelp("Fruit Basket Reporter", options);
                System.exit(0);
            }
            if(cmd.hasOption("help")) {
                formatter.printHelp("Fruit Basket Reporter", options);
                System.exit(0);
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Fruit Basket Reporter", options);
            System.exit(1);
        }

        String report;
        // All the exceptions with data or DB are caught at this level
        try {
            FruitBasketDAO db = new FruitBasketDAO();
            FruitBasketDataIngester ingester = new FruitBasketDataIngester(db);
            FruitBasketReportBuilder reportBuilder = new FruitBasketReportBuilder(db);
            ingester.ingestCSV(csvFile);
            report = reportBuilder.buildReport();
        } catch(DatabaseException de) {
            report = "Not able to process data:\n" + de.getMessage();
        } catch(WrongDataFormatException we) {
            report = "Wrong input data:\n" + we.getMessage();
        }
        System.out.println(report);
    }
}
