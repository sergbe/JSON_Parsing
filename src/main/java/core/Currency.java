package core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;




public class Currency {

    //Method Read_CSV_Files
    public static String[][] Read_CSV(String csvPath) throws IOException, FileNotFoundException
    {
        BufferedReader br_csv = null;
        String line_cs = null;
        String[] column=null;
        int countLines=0;
        int countColumns=0;
        String SplitBy = ";";

//Read CSV files
//Brand;Itme;USD(Corrency Code);Price;Link
// OR
//Country;Currency;Acronym;Convert from USD;IP Address


        //numbers of records (line) in file
        short i=0;
        br_csv = new BufferedReader(new FileReader(csvPath));
        while((line_cs=br_csv.readLine()) !=null)
        {
            countLines++;
            column=line_cs.split(SplitBy); // ";"
            countColumns=column.length;
        }

        br_csv.close();

        String[][] CSV = new String[countLines][countColumns];

        br_csv = new BufferedReader(new FileReader(csvPath));


        while ((line_cs = br_csv.readLine()) != null) {

            String[] csv = line_cs.split(SplitBy);

            //numbers of columns in file
            for(short j=0;j<=countColumns-1;j++)
            {CSV[i][j] = csv[j];}
            i++;
        }

        br_csv.close();

//Print for test
	/*
		for (i=0;i<=countLines-1;i++)
	{
			for (short j=0;j<=countColumns-1;j++)
			{
				System.out.print("\t\t"+CSV[i][j]);

			}
			System.out.println();
	}
	*/

// Returm Array
        return CSV;
    }
//END Method Read_CSV_Files



    //JSON Parsing
//
// 	Exchange_Rate(String ip ,String baseCurrencyCode)
//Looking for county use IP, What currency is Base (USD).  from USD to RUB
    public static float Exchange_Rate(String ip ,String baseCurrencyCode) throws IOException
    {
        float rATE=0;
        String currencyCode=null;
        String countryName=null;

        URL webService= new URL ("http://www.geoplugin.net/json.gp?ip="+ip);

        InputStream stream_location=webService.openStream();
        JsonParser json_location=Json.createParser(stream_location);
        while (json_location.hasNext())
        {
            Event e=json_location.next();
            if(e==Event.KEY_NAME)
            {
                switch (json_location.getString())
                {
                    case "geoplugin_countryName":
                        json_location.next();

                        System.out.print("Country: "+json_location.getString());
                        countryName=json_location.getString();
                        break;

                    case "geoplugin_currencyCode":
                        json_location.next();

                        //System.out.println("Currency Code: "+json_location.getString());
                        currencyCode=json_location.getString();
                        break;
                }
            }
        }



        webService= new URL ("http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20%28%22"+baseCurrencyCode+currencyCode+"%22%29&format=json&env=store://datatables.org/alltableswithkeys");

        stream_location=webService.openStream();
        json_location=Json.createParser(stream_location);
        while (json_location.hasNext())
        {
            Event e=json_location.next();
            if(e==Event.KEY_NAME)
            {
                switch (json_location.getString())
                {
                    case "Name":
                        json_location.next();
                        System.out.print(", Name: "+json_location.getString()+", ");
                        //rATE=json_location.getString();
                        break;

                    case "Rate":
                        json_location.next();
                        rATE=Float.parseFloat(json_location.getString());
                        System.out.print("Rate: "+rATE);
                        break;

                }
            }
        }

        return rATE;
    }

//END JSON Parsing


    public static void main (String[]args) throws FileNotFoundException, IOException{
        //Print CSV


        String [][] Items = Read_CSV("./src/main/resources/Items.csv");
        String [][] Counties = Read_CSV("./src/main/resources/Countries.csv");

//Print for test
//	System.out.println("Items.length "+Items[1].length);
//	System.out.println("Countis.length "+Counties[1].length);

//Read all lines in array 'Items'
        for (int i=0;i<Items.length;i++)
        {

            //Read all columns in array 'Items'
            for(int j=0;j<Items[1].length;j++)
            {
                System.out.print("\t"+Items[i][j]);
            }

            System.out.println();

            //Read all lines in array 'Counties'
            for (int h=0;h<Counties.length;h++)
            {
                //System.out.print(Float.parseFloat(Items[i][3]));
                System.out.print(" --> Local Price: "+Exchange_Rate(Counties [h][4], Items[1][2])*Float.parseFloat(Items[i][3]));
                System.out.println();
            }
            System.out.println("=======================================================================================================");
        }




//END Main
    }


//END of Class
}
