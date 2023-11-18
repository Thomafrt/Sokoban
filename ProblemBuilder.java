package sokoban;

import fr.uga.pddl4j.heuristics.state.StateHeuristic;
import fr.uga.pddl4j.planners.InvalidConfigurationException;
import fr.uga.pddl4j.planners.LogLevel;
import fr.uga.pddl4j.planners.Planner;
import fr.uga.pddl4j.planners.PlannerConfiguration;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.problem.operator.Action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONObject;

public class ProblemBuilder {

	/**
	 * 
	 * @param jsonName
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * @throws InvalidConfigurationException 
	 */
	public static void problemBuild(String jsonName, int levelNumber) throws FileNotFoundException, IOException, ParseException, InvalidConfigurationException {

		String [] lines = jsonToStringArray(jsonName);
		ArrayList<String> places = new ArrayList<String>();
		Boolean in = false; //la boucle est à l'intérieur du niveau

		String define = "(define (problem Sokoban"+levelNumber+")\n(:domain Sokoban)\n";
		String objects = "(:objects ";
		String init = "(:init ";
		String goal = "(:goal (and ";

		for(int i=0; i<lines.length;i++) { //parcours les lignes
			for(int j=0; j<lines[i].length();j++) { //parcours les cases
				if(lines[i].charAt(j)=='#') { //si on rencontre pas de mur
					in=true;
				}
				else if(lines[i].charAt(j)==' ' && in==true) {//une case vide
					places.add("p"+i+j);
					init += "(isEmpty p"+i+j+") ";
				}
				else if(lines[i].charAt(j)=='@' && in==true) {//joueur
					places.add("p"+i+j);
					init += "(playerIsAt p"+i+j+") ";
				}
				else if(lines[i].charAt(j)=='$' && in==true) {//une boite
					places.add("p"+i+j);
					init += "(boxIsAt p"+i+j+") ";
				}
				else if(lines[i].charAt(j)=='.' && in==true) {//une destination vide
					places.add("p"+i+j);
					init += "(isEmpty p"+i+j+") ";
					goal += "(boxIsAt p"+i+j+") ";

				}
				else if(lines[i].charAt(j)=='+' && in==true) {//le joueur sur une destination
					places.add("p"+i+j);
					init += "(playerIsAt p"+i+j+") ";
					goal += "(boxIsAt p"+i+j+") ";
				}
				else if(lines[i].charAt(j)=='*' && in==true) {//une boite sur une destination
					places.add("p"+i+j);
					init += "(boxIsAt p"+i+j+") ";
					goal += "(boxIsAt p"+i+j+") ";
				}
			}
			in=false; //remise à false en fin de ligne
		}

		init += "\n";

		int cpt=0;
		for (String s1 : places) {//pour toutes les cases jouables
			objects += s1+" "; //les ajoute à object

			ArrayList<String> places2 = new ArrayList<String>(places);
			places2.remove(cpt);
			for (String s2 : places2) {
				if(s1.charAt(1) == s2.charAt(1) && s1.charAt(2) == s2.charAt(2)+1) { //si s1 a s2 à sa droite
					//System.out.println(s2+" à cote de "+s1);
					init += "(isRight " +s1+ " " +s2+ ") (isLeft " +s2+ " " +s1+ ") ";
				}
				if(s1.charAt(2) == s2.charAt(2) && s1.charAt(1) == s2.charAt(1)+1) { //si s1 a s2 au dessus de lui
					//System.out.println(s2+" est au dessus de "+s1);
					init += "(isUp " +s1+ " " +s2+ ") (isDown " +s2+ " " +s1+ ") ";
				}
			}
			cpt++;
		}

		objects += "- place)\n";
		init += ")\n";
		goal += ")) )\n";
		String problem = define + objects + init + goal;
		System.out.print("\nLe problème :\n" + problem);

		//écriture du fichier pddl
		final String problemPath = new File(System.getProperty("user.dir")).toPath().resolve("src/main/java/sokoban/problem"+levelNumber+".pddl").toString();
		final String domainPath = new File(System.getProperty("user.dir")).toPath().resolve("src/main/java/sokoban/domain.pddl").toString();

		try {
			File f = new File(problemPath);
			if (f.createNewFile())
				System.out.println("Fichier problème pddl créé");
			else
				System.out.println("Le fichier existe déjà");
			FileWriter fw = new FileWriter(f.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(problem);
			bw.close();
			System.out.println("Fichier rempli");
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		PlannerConfiguration config = HSP.getDefaultConfiguration();
		config.setProperty(HSP.DOMAIN_SETTING, domainPath);
		config.setProperty(HSP.PROBLEM_SETTING, problemPath);
		config.setProperty(HSP.TIME_OUT_SETTING, 1000);
		config.setProperty(HSP.LOG_LEVEL_SETTING, LogLevel.INFO);
		config.setProperty(HSP.HEURISTIC_SETTING, StateHeuristic.Name.MAX);
		config.setProperty(HSP.WEIGHT_HEURISTIC_SETTING, 1.2);
		Planner planner = Planner.getInstance(Planner.Name.HSP, config);

		//création de la String de solution lisible pour l'agent
		String solution = "";
		List<Action> list = planner.solve().actions();
		for (Action a : list) {
			String param1 = places.get(a.getValueOfParameter(0));
			String param2 = places.get(a.getValueOfParameter(1));
			if(param1.charAt(1)==param2.charAt(1) && param1.charAt(2)==param2.charAt(2)+1) { //va à gauche
				solution += "L";
			}
			if(param1.charAt(1)==param2.charAt(1) && param1.charAt(2)==param2.charAt(2)-1) { //va à droite
				solution += "R";
			}
			if(param1.charAt(2)==param2.charAt(2) && param1.charAt(1)==param2.charAt(1)+1) { //va en haut
				solution += "U";
			}
			if(param1.charAt(2)==param2.charAt(2) && param1.charAt(1)==param2.charAt(1)-1) { //va en haut
				solution += "D";
			}
		}
		System.out.println("Solution : " + solution);

		//écriture du fichier solution pour l'agent
		final String solutionPath = new File(System.getProperty("user.dir")).toPath().resolve("src/main/java/sokoban/solution.txt").toString();
		try {
			File f2 = new File(solutionPath);
			if (f2.createNewFile())
				System.out.println("Fichier solution agent créé");
			else
				System.out.println("Le fichier existe déjà");
			FileWriter fw2 = new FileWriter(f2.getAbsoluteFile());
			BufferedWriter bw2 = new BufferedWriter(fw2);
			bw2.write(solution);
	        bw2.newLine();
			bw2.close();
			System.out.println("Fichier rempli");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 
	 * @param jsonName
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	public static String[] jsonToStringArray(String jsonName) throws FileNotFoundException, IOException, ParseException {
		Object jsonObj = new JSONParser().parse(new FileReader("config/"+jsonName));
		JSONObject json = (JSONObject) jsonObj;
		String level = (String)json.get("testIn");
		System.out.println("Le niveau choisi :\n"+level);
		int nb = 0;
		int lastI = 0;
		//calcul de la taille du tableau
		for (int i=0; i < level.length(); i++){
			if (level.charAt(i) == '\n') {
				nb++;
			}
		}
		String levelsArray[] = new String[nb+1];
		//remplissage du tableau
		nb=0;
		for (int i=0; i < level.length(); i++){
			if (level.charAt(i) == '\n') {
				if(nb==0)//la première ligne
					levelsArray[nb]=level.substring(lastI,i);
				else //toutes les autres sauf la dernière
					levelsArray[nb]=level.substring(lastI+1,i);
				lastI = i;
				nb++;
			}
			else {//la dernière ligne
				levelsArray[nb]=level.substring(lastI+1,level.length());
			}
		}
		/*for(int j=0; j<levelsArray.length; j++)
			System.out.println("ligne "+j+":"+levelsArray[j]);*/
		return levelsArray;
	}
}