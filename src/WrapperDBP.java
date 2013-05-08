import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;


public class WrapperDBP {

	public static String URL = "http://example.org/dbpedia/dbpedia";
	public static String NOM = "<http://example.org/dbpedia/townName>";
	public static String CP = "<http://example.org/dbpedia/postalCode>";
	
	public WrapperDBP(){
//		System.setProperty("http.proxyHost", "cache.sciences.univ-nantes.fr");
//		System.setProperty("http.proxyPort", "3128");		
	}

	private static String loadQuery(String path) throws IOException {
		String qString = new String();
		
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line = new String();
		
		while((line = reader.readLine()) != null) {
			qString += line;
		}
		reader.close();
		System.out.println(qString);
		return qString;
	}
	private static String fetchFile(String queryString) {
		// just fetch the xml file to rdf/xml format 
		// now creating query object
		Query query = QueryFactory.create(queryString);
		// initializing queryExecution factory with remote service.
		// **this actually was the main problem I couldn't figure out.**
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
		//after it goes standard query execution and result processing which can
		// be found in almost any Jena/SPARQL tutorial.
		try {
			ResultSet results = qexec.execSelect();
			//File fp = new File("output.csv");
			ByteArrayOutputStream fos = new ByteArrayOutputStream();
            ResultSetFormatter.outputAsCSV(fos, results);
            //on écrit le résultat
            return fos.toString();
		}
		finally {
		   qexec.close();
		}
	}
	
	private void constructModel(){
		 
		
	}
	
	
	public static void main(String[] args) {
		
		//SetProxy px = new SetProxy();

		WrapperDBP w2 = new WrapperDBP();
		String queryPath = new String();
		String outputPath = new String();
		
//		if(args.length < 2) {
//			System.err.println("Warning : argument(s) missing. Using default.");
////			return;
//			queryPath = "/home/seb/Documents/TER/V3/gun2012/code/expfiles/berlinData/DATASET/views/viewsSparql/view9_0.sparql";
//			outputPath = "/home/seb/Documents/TER/V3/gun2012/code/expfiles/berlinData/DATASET/n3dir/view9_0.n3";
//			
//		}
//		else{
			queryPath = args[0];
			outputPath = args[1];			
//		}
		
		try {
			String query = loadQuery(queryPath);
			String csv = fetchFile(query);
			String result = constructN3(csv);
			writeN3(result,outputPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("end of work!");
		
	}

	private static void writeN3(String result, String outputPath) {
		// TODO Auto-generated method stub
		try {
			File file = new File(outputPath);
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(result);
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String constructN3(String csv) {
		// TODO Auto-generated method stub
		String result = new String();
		int id = 0;
		for(String sp: csv.split("\n")){
			if(id != 0){
				String[] s = sp.split(",");
				
				result += "<"+URL+id+">";
				result+= " "+NOM+" \""+s[0]+"\" .\n";
				
				//exception for pc as a string or integer
				String lol = "";
				String temp = s[1];				
				if(temp.charAt(0) == '\"') lol = temp.substring(1,6);
				else lol = temp.substring(0, 5);
				
				result += "<"+URL+id+">";
//				result+= " "+CP+" \""+"85000"+"\" .\n";
				result+= " "+CP+" \""+lol+"\" .\n";
			}
			id++;
		}
		return result;
	}
	
}
