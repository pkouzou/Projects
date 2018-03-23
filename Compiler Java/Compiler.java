import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class Compiler {

	private static final int and_id = 0;
	private static final int declare_id = 1;
	private static final int do_id = 2;
	private static final int else_id = 3;
	private static final int enddeclare_id = 4;
	private static final int exit_id = 5;
	private static final int procedure_id = 6;
	private static final int function_id = 7;
	private static final int print_id = 8;
	private static final int incase_id = 9;
	private static final int if_id = 10;
	private static final int in_id = 11;
	private static final int inout_id = 12;
	private static final int not_id = 13;
	private static final int forcase_id = 14;
	private static final int program_id = 15;
	private static final int or_id = 16;
	private static final int return_id = 17;
	private static final int while_id = 18;
	private static final int copy_id = 18;
	private static final int when_id = 19;
	private static final int add_id = 20;
	private static final int abstract_id = 21;
	private static final int multiply_id = 22;
	private static final int divide_id = 23;
	private static final int assignment_id = 24;
	private static final int equals_id = 25;
	private static final int greater_or_equals_id = 26;
	private static final int less_or_equals_id = 27;
	private static final int greater_id = 28;
	private static final int less_id = 29;
	private static final int semicolon_id = 30;
	private static final int not_equals_id = 31;
	private static final int var_id = 32;
	private static final int constant_id = 33;
	private static final int start_block_id = 34;
	private static final int end_block_id = 35;
	private static final int start_parenthesis_id = 36;
	private static final int end_parenthesis_id = 37;
	private static final int comma_id = 38;
	private static final int boolean_start_id = 39;
	private static final int boolean_end_id = 40;
	private static final int call_id = 41;
	
	
	private static String token;
	private static int token_id;
	private boolean was_semicolon = true;
	private static int temp_line = 0;
	
	private static BufferedReader br;
	private boolean buff_next = true;
	private static int error_line = 1;
	private static int buff_pos = 0;
	
	private int temp_num = 0;
	private int gen_line = 100;
	private static ArrayList<Quad> mainList = new ArrayList<Quad>();
	private String temp_name = "";
	
	private Scope mainscope = new Scope(0);
	private Scope tailscope = mainscope;
	private int num_of_pars = 0;
	private String program_name = "";

	
	Compiler() throws FileNotFoundException{
		br = new BufferedReader(new FileReader("C:/test/test5.st"));
		error_line = 0;
		buff_next = true;
		buff_pos = 0;
		temp_num = 0;
		gen_line = 100;
		was_semicolon = true;
	}
	
	public void getWord() throws IOException{
		
		char s;
		String ss = token;
		token = null;
		String temp = "";

		if(buff_next) buff_pos = br.read();
		
		while(buff_pos != -1){
			
			if(!buff_next){
				token = ss;
				buff_next = true;
			}

			s = (char) buff_pos;
			
			if(s == ' ' || s == '\n' || s == '\r' || s == '\t'){
				
				if(s == '\n') error_line++;
				if(!temp.isEmpty()){
					findWord(temp);
					return;
				}
			
				else{
					buff_pos = br.read();
					s = (char) buff_pos;
					while(s == ' ' || s == '\n' || s == '\r' || s == '\t' || s == '\n'){
						if(s == '\n') error_line++;
						buff_pos = br.read();
				
						s = (char) buff_pos;
					}
				}
			}
			
			//recognizes comments
			if(s == '/'){
				if(!temp.isEmpty()){
					findWord(temp);
					buff_next = false;
					return;
				}
				
				buff_pos = br.read();
				s = (char) buff_pos;
				
				if(s != '*'){
					findWord("/");
					buff_next = false;
					return;
				}
				else{
					char t ;

						s = (char)  buff_pos;
						while(buff_pos != -1){
							t = s;
							buff_pos = br.read();
							s = (char) buff_pos;
							if(s == '\n') error_line++; 
							if(s == '/'){
								if(t == '*'){
									
									getWord();
									return;	
								}
							}
						}
						System.out.println("Error! No comment block close properly " + "(line " + error_line + ")");
						System.exit(1);
				}
			}
			
			if(s == '+' || s == '-' || s == '/' || s == '*' || s == '{' || s == '}' || s ==';' || s =='(' || s == ')' || s == '=' || s == ',' || s == '[' || s == ']'){
				if(!temp.isEmpty()){
					findWord(temp);
					buff_next = false;
					return;
				}
				token = s+"";
				token_id = findId(token);
				return;
			}
			
			if(s == '>' || s == '<'){
				if(temp.isEmpty()){
					temp = "" +s;
					char t = s;
					if((buff_pos = br.read()) != -1){
						s = (char) buff_pos;
						if(s != '='){
							if(t == '<' && s == '>'){
								token = "<>";
								token_id = findId(token);
								return;
							}
							token = temp;
							token_id = findId(token);
							buff_next = false;
							return;
						}
						else{
							token = temp + "=";
							token_id = findId(token);
							return;
						}
					}
					else{
						token = temp;
						token_id = findId(token);
						return;
					}
				}
				else{
					buff_next = false;
					findWord(temp);
				
					return;
				}
			}
			else if(s == ':'){
				if(temp.isEmpty()){
					temp = ""+s;
					buff_pos = br.read();
					if(buff_pos != -1){
						s = (char) buff_pos;
						if(s != '='){
							System.out.println("Error! '=' expected after ':'"  + " (line " + error_line + ")");
							System.exit(1);
						}
						else{
							token = ":=";
							token_id = findId(token);
							return;
						}
					}
					else{
						System.out.println("Error! '=' expected after ':'" + " (line " + error_line + ")");
						System.exit(1);
					}
				}
				else{
					
					buff_next = false;
					findWord(temp);
					return;
				}
			}
			
			if(!((s >= 'a' && s <= 'z') || (s >= 'A' && s <= 'Z')) && !(s >= '0' && s <= '9') && buff_pos != -1){
				System.out.println("Error! Unavailable character '" + s  + "' (line " + error_line + ")");
				System.exit(1);
			}
			
			temp += s;
			buff_pos = br.read();	
		}
		
		//end of file
		if(buff_pos == -1){
			if(!temp.isEmpty()){
				findWord(temp);
			}
			token = null;
			token_id = -1;
		}
	}
	
	public int findId(String s){
			
		if(s.equals("and")){
			return and_id;
		}
		else if(s.equals("declare")){
			return declare_id;
		}
		else if(s.equals("do")){
			return do_id;
		}
		else if(s.equals("else")){
			return else_id;
		}
		else if(s.equals("enddeclare")){
			return enddeclare_id;
		}
		else if(s.equals("exit")){
			return exit_id;
		}
		else if(s.equals("procedure")){
			return procedure_id;
		}
		else if(s.equals("function")){
			return function_id;
		}
		else if(s.equals("print")){
			return print_id;
		}
		else if(s.equals("incase")){
			return incase_id;
		}
		else if(s.equals("if")){
			return if_id;
		}
		else if(s.equals("in")){
			return in_id;
		}
		else if(s.equals("inout")){
			return inout_id;
		}
		else if(s.equals("not")){
			return not_id;
		}
		else if(s.equals("forcase")){
			return forcase_id;
		}
		else if(s.equals("program")){
			return program_id;
		}
		else if(s.equals("call")){
			return call_id;
		}
		else if(s.equals("or")){
			return or_id;
		}
		else if(s.equals("return")){
			return return_id;
		}
		else if(s.equals("while")){
			return while_id;
		}
		else if(s.equals("copy")){
			return copy_id;
		}
		else if(s.equals("when")){
			return when_id;
		}
		else if(s.equals("+")){
			return add_id;
		}
		else if(s.equals("-")){
			return abstract_id;
		}
		else if(s.equals("*")){
			return multiply_id;
		}
		else if(s.equals("/")){
			return divide_id;
		}
		else if(s.equals(":=")){
			return assignment_id;
		}
		else if(s.equals("=")){
			return equals_id;
		}
		else if(s.equals(">=")){
			return greater_or_equals_id;
		}
		else if(s.equals("<=")){
			return less_or_equals_id;
		}
		else if(s.equals(">")){
			return greater_id;
		}
		else if(s.equals("<")){
			return less_id;
		}
		else if(s.equals(";")){
			return semicolon_id;
		}
		else if(s.equals("<>")){
			return not_equals_id;
		}
		else if(s.equals("var")){
			return var_id;
		}
		else if(s.equals("constant")){
			return constant_id;
		}
		else if(s.equals("constant")){
			return constant_id;
		}else if(s.equals("{")){
			return start_block_id;
		}
		else if(s.equals("}")){
			return end_block_id;
		}else if(s.equals("(")){
			return start_parenthesis_id;
		}
		else if(s.equals(")")){
			return end_parenthesis_id;
		}
		else if(s.equals(",")){
			return comma_id;
		}
		else if(s.equals("[")){
			return boolean_start_id;
		}
		else if(s.equals("]")){
			return boolean_end_id;
		}
		
		return -1;	
	}
	
	public void findWord(String str) {
		
		if(isInteger(str)){
			if(Integer.parseInt(str) < (-1)*32768 || Integer.parseInt(str) > 32767){
	        	System.out.println("Error! Constant not in rage (-32768, 32767) "  + "(line " + error_line + ")");
	        	return;
	        }
	        token = str;
	        token_id = findId("constant");
	        return;
		}

	    if(isInteger(str.charAt(0)+"") && str.length() > 1){
    		System.out.println("Error! Cannot have variable name starting with number "  + "(line " + error_line + ")");
    		return;
    	}
	    token = str;
	    token_id = findId(token);
	    if(token_id == -1){
	    	token_id = var_id;

	    } 
	}

	public boolean isInteger(String k){
		try {
	        Integer.parseInt(k);
	        return true;
	    } catch (NumberFormatException nfe) {}
		return false;
	}
	
	 /*******************************************/
	/*******************STATS*******************/
   /*******************************************/
	
public void program_stat() throws IOException{
		
		if(token_id == program_id){
			getWord();
			if(token_id == var_id){
				String name[] = new String[1];
				name[0] = token;
				program_name = token;
				getWord();
				System.out.println("L1: jmp L0");
				block_stat(true, name, 1);
			}
			else{
				System.out.println("Syntax error, program name was expected "  + "(line " + error_line + ")");
				System.exit(1);
			}
		}
		else{
			System.out.println("Syntax error, The keyword 'program' was expected "  + "(line " + error_line + ")");
			System.exit(1);
		}
	}
	
public void block_stat(boolean isProgram, String name[], int start) throws IOException{
		
		if(token_id == start_block_id){
			getWord();
			declarations_stat();
			genquad("begin_block", name[0], "_", "_");
			subprograms_stat();
			sequence_stat(null);
			
			if(token_id == end_block_id){
				genquad("end_block", name[0], "_", "_");
	
				if(isProgram) {
					
					
					genquad("halt", "_", "_", "_");
					
					int N = mainList.size();
					for(int i = 0; i < N; i++){
						
						generateEndCode(mainList.get(0));
						mainList.remove(0);
					}		
				}
				
				if(!isProgram){
					int startquad = mainList.get(start).getLabel();
					Entity e = searchEntity(name[0]);
					((Function)e).setStartquad(startquad);
					int N = mainList.size();
				
					Scope temp = mainscope;
					while(temp.getNestingLevel() != tailscope.getNestingLevel()-1){
						temp = temp.getNext();
					}

					for(int i = start; i < N; i++){
						
						
						generateEndCode(mainList.get(start));
						mainList.remove(start);
					}

					tailscope = tailscope.deleteScope(mainscope);
				}
				getWord();
			}
			else{
				System.out.println(token);
				System.out.println("Syntax error, '}' was expected (program block) "  + "(line " + error_line + ")");
				System.exit(1);
			}
		}
		else{
			System.out.println("Syntax error, '{' was expected (program block)"  + "(line " + error_line + ")");
			System.exit(1);
		}
	}

	public void declarations_stat() throws IOException{
	
		if(token_id == declare_id){
			getWord();
			varlist_stat();
			if(token_id == enddeclare_id){
				getWord();
			}else{
				
				System.out.println("Syntax error, enddeclare was expected "   +"(line " + error_line + ")");
				System.exit(1);
			}
		}
	}
	
	public void varlist_stat() throws IOException{
		
		if(token_id == var_id){
			tailscope.addEntity(new Variable(token, "int", tailscope.getNextOffset(), tailscope.getNextOffset(), tailscope.getNestingLevel()));
			tailscope.incNextOffset();
			getWord();
			while(token_id == comma_id){
				getWord();
				if(token_id == var_id){
					tailscope.addEntity(new Variable(token, "int", tailscope.getNextOffset(), tailscope.getNextOffset(), tailscope.getNestingLevel()));
					tailscope.incNextOffset();
					getWord();
				}
				else{
					System.out.println("Syntax error, 'id' was expected after ',' "  + "(line " + error_line + ")");
					System.exit(1);
				}
			}
		}
	}
	
	public void subprograms_stat() throws IOException{
		
		while(token_id == procedure_id || token_id == function_id){
			
			int start = mainList.size();
			func_stat(start);
		}
	}

	public void func_stat(int start) throws IOException{
	
	String s = token;
	String return_type;
	
	
	if(s.equals("function")) return_type = "int";
	else return_type = "void";
	
		getWord();
		if(token_id == var_id) {
			String name[] = new String[1];
			name[0] = token;
			
			tailscope.addEntity(new Function(token, return_type, null, 100+nextquad()*10, tailscope.getNextOffset(), tailscope.getNestingLevel()+1));//EDIT
			
			getWord();
			funcbody_stat(name, start);
		}
		else{
			System.out.println("Syntax error, 'Id' was expected after '" + s  +   "(line " + error_line + ")");
			System.exit(1);
		}
	
	}
	
	public void funcbody_stat(String name[], int start) throws IOException{
		formalpars_stat();
		block_stat(false, name, start);
	}
	
	public void formalpars_stat() throws IOException{
		
		if(token_id == start_parenthesis_id){
			ArrayList<Argument> args = new ArrayList<Argument>();
			Scope tempscope1 = tailscope;
			Scope tempscope2 = new Scope(tailscope.getNestingLevel()+1);
			tailscope.setNext(tempscope2);
			tailscope = tempscope2;
			getWord();
			if(token_id == end_parenthesis_id) {
				getWord(); 
				return;
			}
			
			formalparlist_stat(args);
			
			if(token_id == end_parenthesis_id) {
			
				tempscope1.getEntity().setArgs(args, (Function) tempscope1.getEntity());
				getWord();
			}
			else{
				System.out.println("Syntax error, ')' was expected "  + "(line " + error_line + ")");
				System.exit(1);
			}
		}
		else{
			System.out.println("Syntax error, '(' was expected "  + "(line " + error_line + ")");
			System.exit(1);
		}
	}
	
	public void formalparlist_stat(ArrayList<Argument> args) throws IOException{
		
		formalparitem_stat(args);
		
		while(token_id == comma_id){
			
			getWord();
			formalparitem_stat(args);
		}
	}
	
	public void formalparitem_stat(ArrayList<Argument> args) throws IOException{
		
		String s = token;
		
		if(token_id == in_id || token_id == inout_id || token_id == copy_id){
			
			int temp = token_id;
			
			getWord();
			if(token_id == var_id) {
				if(temp == in_id) {
					args.add(new Argument("CV", "int"));
					tailscope.addEntity(new Parameter(token, "int", tailscope.getNextOffset(), "CV", tailscope.getNextOffset(), tailscope.getNestingLevel()));
					tailscope.incNextOffset();
				}
				else if(temp == inout_id) {
					args.add(new Argument("REF", "int"));
					tailscope.addEntity(new Parameter(token, "int", tailscope.getNextOffset(), "REF", tailscope.getNextOffset(), tailscope.getNestingLevel()));
					tailscope.incNextOffset();
				}
				else {
					args.add(new Argument("CP", "int"));
					tailscope.addEntity(new Parameter(token, "int", tailscope.getNextOffset(), "CP", tailscope.getNextOffset(), tailscope.getNestingLevel()));
					tailscope.incNextOffset();
				}
				getWord();
			}
			else{
				System.out.println("Syntax error, 'Id' was expected after '" + s  +   "(line " + error_line + ")");
				System.exit(1);
			}
			
		}
		
		else{
			System.out.println(token);
			System.out.println("Syntax error, The keyword 'in,inout or copy' was expected "  + "(line " + error_line + ")");
			System.exit(1);
		}
	}
	
	public void sequence_stat(ArrayList<Integer> exitList) throws IOException{
	
		
		statement_stat(exitList);
		
		if(token_id != semicolon_id) was_semicolon = false;
		if(!was_semicolon){
			if(nextStatement()){
				System.out.println("Syntax error, ';' was expected (line " + temp_line + ")");
				System.exit(1);
			}
		}
		
		while(token_id == semicolon_id){
			
			
			temp_line = error_line;
			getWord();
			statement_stat(exitList);
			if(token_id != semicolon_id) was_semicolon = false;
		}
		
		if(!was_semicolon){
			if(nextStatement()){
				System.out.println("Syntax error, ';' was expected (line " + temp_line + ")");
				System.exit(1);
			}
		}
	}
	
	public void brackets_seq_stat(ArrayList<Integer> exitList) throws IOException{
		
		if(token_id == start_block_id){
			
			getWord();
			sequence_stat(exitList);
				
			if(token_id != end_block_id){
				System.out.println(token);
				System.out.println("Syntax error, '}' was expected (line " + error_line + ")");
				System.exit(1);
			}
			getWord();
		}
	}
	
	public void brack_or_stat(ArrayList<Integer> exitList) throws IOException{
		
	
		if(token_id == start_block_id){
		
			brackets_seq_stat(exitList);
			return;
		}
			
		else {
			
			if(nextStatement()) statement_stat(exitList);	
			if(token_id == semicolon_id) getWord();
			if(!nextStatement() && token_id != end_block_id) {
				System.out.println("Syntax error, Statement was expected (line " + temp_line + ")");
				System.exit(1);
			}
		}
	}
	
	public void statement_stat(ArrayList<Integer> exitList) throws IOException{
	
		temp_line = error_line;
		if(token_id == var_id) assignment_stat();
		else if (token_id == if_id) if_stat();
		else if (token_id == while_id) while_stat();
		else if (token_id == do_id) do_while_stat();
		else if (token_id == exit_id) exit_stat(exitList);
		else if (token_id == incase_id) incase_stat();
		else if (token_id == forcase_id) forcase_stat();
		else if (token_id == call_id) call_stat();
		else if (token_id == return_id) return_stat();
		else if (token_id == print_id) print_stat();
	}
	
	public void assignment_stat() throws IOException{
	
		String id = token;
		getWord();
		
		if(token_id == assignment_id){
		
			getWord();
			temp_name = token;
			
			if(token_id == var_id || token_id == constant_id){
		
				String term[] = new String[1];
				expression_stat(term);
				genquad(":=", term[0], "_",id);	
			}	
		}
		else{
			
			System.out.println("Syntax error, ':=' was expected after the id (line " + error_line + ")");
			System.exit(1);
		}
	}
	
	public void if_stat() throws IOException{
		
		ArrayList<Integer> Btrue = new ArrayList<Integer>();
		ArrayList<Integer> Bfalse = new ArrayList<Integer>();
		
		getWord();
		if(token_id == start_parenthesis_id){	
			getWord();
			condition_stat(Btrue,Bfalse);
			
			if(token_id == end_parenthesis_id) getWord();
			else{
				System.out.println("Syntax error, ')' was expected (line " + error_line + ")");
				System.exit(1);
			}
			
			brack_or_stat(null);
			ArrayList<Integer> iflist = makeList(nextquad());
			genquad("jump", "_", "_", "_");
			backpatch(Bfalse, nextquad());
			
			elsepart_stat(iflist);
		}
		else{
			
			System.out.println("Syntax error, '(' was expected after 'if' (line " + error_line + ")");
			System.exit(1);
		}
	}
	
	public void elsepart_stat(ArrayList<Integer> iflist) throws IOException{
		
		if(token_id == else_id){
			getWord();
			brack_or_stat(null);
		}
		backpatch(iflist, nextquad());
	}
	
	public void do_while_stat() throws IOException{
		
		ArrayList<Integer> condtrue = new ArrayList<Integer>();
		ArrayList<Integer> condfalse = new ArrayList<Integer>();
		
		int sQuad = nextquad();
		
		getWord();
		brack_or_stat(null);
		if(token_id == while_id){
			getWord();
			if(token_id == start_parenthesis_id){
				getWord();
				condition_stat(condtrue,condfalse);
				if(token_id == end_parenthesis_id) {
					
					backpatch(condtrue, sQuad);
					getWord();
				}
				else{
					System.out.println("Syntax error, ')' was expected after condition (line " + error_line + ")");
					System.exit(1);
				}
			}
			else{
				System.out.println("Syntax error, '(' was expected after 'while' (line " + error_line + ")");
				System.exit(1);
			}
		}
		else{
			
			if(token == null){
				System.out.println("Syntax error, '}' was expected (line " + temp_line + ")");
				System.exit(1);
			}
			
			if(token_id == end_block_id || token_id == semicolon_id){
				System.out.println("Syntax error, '{' was expected (line " + temp_line + ")");
				System.exit(1);
			}
		}
		
	}
	
	public void exit_stat(ArrayList<Integer> exitList) throws IOException{
		
		getWord();
		if(exitList == null) return;
		ArrayList<Integer> x = makeList(nextquad());
		ArrAssignment(merge(exitList, x), exitList);
		
		genquad("jump","_","_","_");
	}
	
	public void return_stat() throws IOException{
		
		getWord();
		if(token_id == start_parenthesis_id){
			
			String term[] = new String[1];
			getWord();
			expression_stat(term);
			
			if(token_id == end_parenthesis_id){
				
				genquad("retv", term[0], "_", "_");
				getWord();
			}
			else{
				System.out.println("Syntax error, ')' was expected (line " + error_line + ")");
				System.exit(1);
			}
		}
		else{
			System.out.println("Syntax error, '(' was expected (line " + error_line + ")");
			System.exit(1);
		}
	}
	
	public void print_stat() throws IOException{
		
		getWord();
		if(token_id == start_parenthesis_id){
			
			String term[]=  new String[1];
			getWord();
			expression_stat(term);
			
			if(token_id == end_parenthesis_id){
				
				genquad("out", term[0], "_", "_");
				getWord();
			}
			else{
				System.out.println("Syntax error, ')' was expected (line " + error_line + ")");
				System.exit(1);
			}
		}
		else{
			System.out.println("Syntax error, '(' was expected (line " + error_line + ")");
			System.exit(1);
		}
	}
	
	public void while_stat() throws IOException{
		
		ArrayList<Integer> Btrue = new ArrayList<Integer>();
		ArrayList<Integer> Bfalse = new ArrayList<Integer>();
		ArrayList<Integer> exitList = new ArrayList<Integer>();
		int Bquad = nextquad()*10+100;
		
		getWord();
		if(token_id == start_parenthesis_id){
			getWord();
			condition_stat(Btrue,Bfalse);
			if(token_id == end_parenthesis_id){
				getWord();
				brack_or_stat(exitList);
				genquad("jump","_", "_", ""+Bquad);
				backpatch(Bfalse, nextquad());
			}
			else{
				System.out.println("Syntax error, ')' was expected after condition (line " + error_line + ")");
				System.exit(1);
			} 
		}
		else{
			System.out.println("Syntax error, '(' was expected after 'while' (line " + error_line + ")");
			System.exit(1);
		}
		
		backpatch(exitList, nextquad());
	}
	
	public void incase_stat() throws IOException{
		
		ArrayList<Integer> condtrue = new ArrayList<Integer> ();
		ArrayList<Integer> condfalse = new ArrayList<Integer> ();
		int p0_quad = nextquad();
		genquad(":=", "1", "_", "flag_");
		
		getWord();
		if(token_id == start_block_id){
			getWord();
			while(token_id == when_id){
				temp_line = error_line;
				getWord();
				if(token_id == start_parenthesis_id){
					getWord();
					condition_stat(condtrue,condfalse);
					
					if(token_id == end_parenthesis_id){
						
						backpatch(condtrue, nextquad());
						genquad(":=", "0", "_", "flag_");
						
						getWord();
						brack_or_stat(null);
						
						
						if(token_id == end_block_id){
							backpatch(condfalse, nextquad());
							getWord();
						}
						else{
							System.out.println("Syntax error, '}' was expected (line " + temp_line + ")");
							System.exit(1);
						}
					}
					else{
						System.out.println("Syntax error, ')' was expected (line " + temp_line + ")");
						System.exit(1);
					}
				}
				else{
					System.out.println("Syntax error, '(' was expected (line " + temp_line + ")");
					System.exit(1);
				}
			}
			
			genquad(":=", "0", "flag_", ""+p0_quad);
		}
		else{
			System.out.println("Syntax error, '{' was expected  (line " + temp_line + ")");
			System.exit(1);
		}
	}
	
	public void forcase_stat() throws IOException{
		
		ArrayList<Integer> condtrue = new ArrayList<Integer> ();
		ArrayList<Integer> condfalse = new ArrayList<Integer> ();
		int start = nextquad();
		
		getWord();
		if(token_id == start_block_id){
			getWord();
			while(token_id == when_id){
				getWord();
				if(token_id == start_parenthesis_id){
					getWord();
					condition_stat(condtrue,condfalse);
					
					if(token_id == end_parenthesis_id){
						backpatch(condtrue, nextquad());
						getWord();
						brack_or_stat(null);
						
						
						if(token_id == end_block_id){
							genquad("jump", "_", "_", ""+start);
							backpatch(condfalse, nextquad());
							getWord();
						}
						else{
							System.out.println("Syntax error, '}' was expected (line " + error_line + ")");
							System.exit(1);
						}
					}
					else{
						System.out.println("Syntax error, ')' was expected  (line " + error_line + ")");
						System.exit(1);
					}
				}
				else{
					System.out.println("Syntax error, '(' was expected (line " + error_line + ")");
					System.exit(1);
				}
			}
		}
		else{
			System.out.println("Syntax error, '{' was expected (line " + error_line + ")");
			System.exit(1);
		}	
	}
	
	public void call_stat() throws IOException{
		
		getWord();
		if(token_id != var_id){
			System.out.println("Syntax error, ID was expected "  + "(line " + temp_line + ")");
			System.exit(1);
		}
		String name = token;
		getWord();
		actualpars_stat();
		genquad("call", name, "_", "_");
	}
	
	public void actualpars_stat() throws IOException{
		
		
		if(token_id == start_parenthesis_id){
			temp_line = error_line;
			getWord();
			actualparlist_stat();
			
			if(token_id == end_parenthesis_id){
				getWord();
			}
			else{
				System.out.println("Syntax error, ')' was expected "  + "(line " + temp_line + ")");
				System.exit(1);
			}	
		}
	}
	
	public void actualparlist_stat() throws IOException{
		
		ArrayList<String> pars = new ArrayList<String>();
		
		if(token_id == in_id) actualparitem_stat(pars, false);
		else actualparitem_stat(null, false);
		
		while(token_id == comma_id){
			getWord();
			if(token_id == in_id) actualparitem_stat(pars, true);
			else actualparitem_stat(null, true);
		}
		
		for(int i = 0; i < pars.size(); i++) genquad("par", pars.get(i), "CV", "_");
	}
	
	public void actualparitem_stat(ArrayList<String> pars, boolean comma) throws IOException{
		
		
		if(token_id == in_id){
			
			getWord();
			String e[] = new String[1];
			expression_stat(e);
			pars.add(e[0]);
			
		}
		else if (token_id == inout_id){
			
			getWord();
			if(token_id == var_id){
				genquad("par", token, "REF", "_");
				getWord();
			}
			else{
				System.out.println("Syntax error, 'Id' was expected after the keyword 'inout' "  + "(line " + error_line + ")");
				System.exit(1);
			}
		}
		else if (token_id == copy_id){
			
			getWord();
			if(token_id == var_id){
				genquad("par", token, "CP", "_");
				getWord();
			}
			else{
				System.out.println("Syntax error, 'Id' was expected after the keyword 'copy' "  + "(line " + error_line + ")");
				System.exit(1);
			}
			
		}
		else if(comma){
			System.out.println("Syntax error, 'in,inout or copy' was expected "  + "(line " + error_line + ")");
			System.exit(1);
		}
	}
	
	public void condition_stat(ArrayList<Integer> Qtrue, ArrayList<Integer> Qfalse) throws IOException{
		
		ArrayList<Integer> Btrue = new ArrayList<Integer>();
		ArrayList<Integer> Bfalse =  new ArrayList<Integer>();

		boolterm_stat(Btrue, Bfalse);

		ArrAssignment(Btrue, Qtrue);
		ArrAssignment(Bfalse, Qfalse);
		
		while(token_id == or_id){
			backpatch(Bfalse, nextquad());
			getWord();
			boolterm_stat(Btrue, Bfalse);	
			ArrAssignment(merge(Btrue, Qtrue), Qtrue);
			if(Qfalse != null) ArrAssignment(Bfalse, Qfalse);
		}
		
		backpatch(Qtrue, nextquad());
	}
	
	public void boolterm_stat(ArrayList<Integer> Rtrue, ArrayList<Integer> Rfalse) throws IOException{
		
		ArrayList<Integer> Qtrue = new ArrayList<Integer>();
		ArrayList<Integer> Qfalse = new ArrayList<Integer>();
		
		boolfactor_stat(Qtrue, Qfalse);
		
		ArrAssignment(Qtrue, Rtrue);
		ArrAssignment(Qfalse, Rfalse);

		while(token_id == and_id){
			
			backpatch(Qtrue, nextquad());
			getWord();
			boolfactor_stat(Qtrue, Qfalse);
			ArrAssignment(merge(Qfalse, Rfalse), Rfalse);
			if(Rtrue != null) ArrAssignment(Qtrue, Rtrue);
		}
	}
	
	public void boolfactor_stat(ArrayList<Integer> Btrue, ArrayList<Integer> Bfalse) throws IOException{
		
		ArrayList<Integer> Rtrue = new ArrayList<Integer>(), Rfalse = new ArrayList<Integer>();
		
		if(token_id == not_id){
			getWord();
			
			if(token_id != boolean_start_id){
				System.out.println("Syntax error, '[' was expected "  + "(line " + error_line + ")");
				System.exit(1);
			}
			getWord();
			condition_stat(Rfalse, Rtrue);
			if(token_id == boolean_end_id){
				ArrAssignment(Rtrue, Btrue);
				ArrAssignment(Rfalse, Bfalse);
				getWord();
			}
			else{
				System.out.println("Syntax error, ']' was expected "  + "(line " + error_line + ")");
				System.exit(1);
			}
		}
		else if (token_id == boolean_start_id){
			getWord();
			condition_stat(Rtrue,Rfalse);
			if(token_id == boolean_end_id){
				ArrAssignment(Rtrue, Btrue);
				ArrAssignment(Rfalse, Bfalse);
				getWord();
			}
			else{
				System.out.println("Syntax error, ']' was expected "  + "(line " + error_line + ")");
				System.exit(1);
			}
		}
		else{
			String e1[] = new String[1];
			expression_stat(e1);
			String op[] = new String[1];
			relational_oper_stat(op);
			String e2[] = new String[1];
			expression_stat(e2);
		
			Rtrue = makeList(nextquad());
			genquad(op[0], e1[0], e2[0], "_");
			Rfalse = makeList(nextquad());
			genquad("jump", "_", "_", "_");
		
			ArrAssignment(Rtrue, Btrue);
			ArrAssignment(Rfalse, Bfalse);	
		}	
	}
	
	public void expression_stat(String term[]) throws IOException{
		
		String term1[] = new String[1];
		String sign = optional_sign_stat();
		if(sign != null){
			if(sign.equals("+")) sign = "";
		}
		else sign = "";
		term_stat(term1);
		
		term1[0] = sign + term1[0];
		while(token_id == add_id || token_id == abstract_id){
			
			String op = token;
			getWord();
			String term2[] = {token};
			term_stat(term2);
			
			String w = newtemp();
			genquad(op, term1[0], term2[0], w);
			term1[0] = w;
			
		}
		
		if(term != null) term[0] = term1[0];
	}
	
	public void term_stat(String term[]) throws IOException{
		
		factor_stat(term);
		
		String term2[]= new String[1];
		while(token_id == multiply_id || token_id == divide_id){
			
			String op = token;
			getWord();
			term2[0] = token;
		
			factor_stat(term2);
			
			String w = newtemp();
			genquad(op, term[0], term2[0], w);
			term[0] = w;	
		}
	}
	
	public void factor_stat(String term[]) throws IOException{
		
		if(token_id == constant_id){
			
			term[0] = token;
			getWord();
		}
		else if (token_id == start_parenthesis_id){
			

			getWord();
			expression_stat(term);
			if(token_id == end_parenthesis_id){
				
				getWord();
			}
			else{
				System.out.println("Syntax error, ')' was expected "  + "(line " + error_line + ")");
				System.exit(1);
			}
		}
		else if (token_id == var_id){
			
			term[0] = token;
			getWord();
			int id = token_id;
			idtail_stat();	
			if(id == start_parenthesis_id){
				String w = newtemp();
				
				genquad("par", w, "RET", "_");
				genquad("call", temp_name, "_", "_");
				
				term[0] = w;
			}
		}
	}
	
	public void idtail_stat() throws IOException{
		
		actualpars_stat();
		
	}
	
	public void relational_oper_stat(String[] relop) throws IOException{
		
		if(token_id == equals_id || token_id == greater_id || token_id == less_id || token_id == greater_or_equals_id || token_id == less_or_equals_id || token_id == not_equals_id){
			
			relop[0] = token;
			getWord();
		}
	}
	
	public String add_oper_stat() throws IOException{
		
		if(token_id == add_id || token_id == abstract_id){
			String op = token;
			getWord();
			return op;
		}
		return null;
	}
	
	public void mul_oper_stat() throws IOException{
		
		
		if(token_id == multiply_id || token_id == divide_id)
		getWord();
	}
	
	public String optional_sign_stat() throws IOException{
		
		return add_oper_stat();
	}
	
	public boolean nextStatement(){
		if(token_id == var_id) return true;
		else if (token_id == if_id) return true;
		else if (token_id == while_id) return true;
		else if (token_id == do_id) return true;
		else if (token_id == exit_id) return true;
		else if (token_id == incase_id) return true;
		else if (token_id == forcase_id) return true;
		else if (token_id == call_id) return true;
		else if (token_id == return_id) return true;
		else if (token_id == print_id) return true;
		return false;
	}
	
	public int nextquad(){
		return mainList.size();
	}
	
	public void genquad(String op, String x, String y, String z){
		mainList.add(new Quad(gen_line,op,x,y,z));
		gen_line += 10;
	}
	
	public String newtemp(){
		
		temp_num++;
		int k = tailscope.getEntities().get(tailscope.getEntities().size()-1).getNestingLevel();
		
		tailscope.addEntity(new Temp("t_" + temp_num, tailscope.getNextOffset(), tailscope.getNextOffset(), k));
		tailscope.incNextOffset();
		return "t_" + temp_num;
	}
	
	public ArrayList<Integer> emptylist(){
		return new ArrayList<Integer>();
	}
	
	public ArrayList<Integer> makeList(int x){
		
		ArrayList<Integer> a = new ArrayList<Integer>();
		a.add(x);
		return a;
	}
	
	public ArrayList<Integer> merge(ArrayList<Integer> list1, ArrayList<Integer> list2){
		
		if(list1 == null) return list2;
		if(list2 == null) return list1;
		
		ArrayList<Integer> list3 = new ArrayList<Integer>();
		
		for(int i = 0; i < list1.size(); i++){
			list3.add(list1.get(i));
		}
		for(int i = 0; i < list2.size(); i++){
			list3.add(list2.get(i));
		}
		return list3;
	}
	
	public void backpatch(ArrayList<Integer> list, int z){
	
		if(list == null) return;
		for(int i = 0; i < list.size(); i++){
			mainList.get(list.get(i)).setZ("" + (100+10*z));
		}
	}
	
	//creates a copy of an ArrayList
		public void ArrAssignment(ArrayList<Integer> arr1, ArrayList<Integer> arr2){
			
			arr2.clear();
			for(Integer k : arr1){
				arr2.add(k);
			}
		}	
		
		public void printScope(){
			mainscope.print();
		}
		
		
		public Entity searchEntity(String name){//type variable, function etc
			
			Scope s = mainscope;
			ArrayList<Entity> e;
			Entity ret = null;
			while(s != null){
				e = s.getEntities();
				for(Entity ent : e){
					if(ent.getName().equals(name)){
						ret = ent;
					}
				}
				s = s.getNext();
				
			}
			
			if(ret == null){
				System.out.println("ERROR, " + name + ": " + name + " not found");
				System.exit(1);
			}
			
			return ret;
		}
		
		public void gnlvcode(String x){
			
			Entity e = searchEntity(x);
			int curScopeLevel = tailscope.getNestingLevel();
			int eScopeLevel = e.getNestingLevel();
			while(curScopeLevel != eScopeLevel){
				System.out.println("movi R[255],M[4+R[255]]");
				eScopeLevel++;
			}
			System.out.println("movi R[254], " + e.getOffset());
			System.out.println("addi R[255], R[254], R[255]");
		}
		
		
		public void loadvr(String v,int r){
			
			if(isInteger(v)){
				System.out.println("movi R[" + r + "], " + v);
				return;
			}
			
			Entity e = searchEntity(v);
			int curScopeLevel = tailscope.getNestingLevel();
			int eScopeLevel = e.getNestingLevel();
			
			if(eScopeLevel == 0){
				System.out.println("movi R[" + r + "], M[" + (600+ + e.getOffset()) + "]");
			}
			else if(curScopeLevel == eScopeLevel){
				
				
				if(e.getType().equals("Parameter")){
					if(((Parameter)e).getParmode().equals("CV")){
						System.out.println("movi R[" + r + "], M[" + e.getOffset() + "+R[0]]");
					}
					else if(((Parameter)e).getParmode().equals("REF")){
						System.out.println("movi R[255], M[" + e.getOffset() + "+R[0]]");
						System.out.println("movi R[" + r + "], M[R[255]]");
					}
				}
				else if(e.getType().equals("Variable") || e.getType().equals("Temp")){
					System.out.println("movi R[" + r + "], M[" + e.getOffset() + "+R[0]]");
				}
			}
			else if(eScopeLevel < curScopeLevel){
				if(e.getType().equals("Parameter")){
					if(((Parameter)e).getParmode().equals("CV")){
						gnlvcode(v);
						System.out.println("movi R[" + r + "], M[R[255]]");
					}
					else if(((Parameter)e).getParmode().equals("REF")){
						gnlvcode(v);
						System.out.println("movi R[255], M[R[255]]");
						System.out.println("movi R[" + r + "], M[R[255]]");
					}
				}
				else if(e.getType().equals("Variable")){
					gnlvcode(v);
					System.out.println("movi R[" + r + "], M[R[255]]");
				}
			}
		}
		
		public void storerv(int r, String v){
			
			if(isInteger(v)){
				System.out.println("movi R[" + r + "], " + v);
				return;
			}
			
			Entity e = searchEntity(v);
			int curScopeLevel = tailscope.getNestingLevel();
			int eScopeLevel = e.getNestingLevel();
			
			if(eScopeLevel == 0){
				System.out.println("movi M[" + (600 + e.getOffset()) + "], R[" + r + "]");
			}
			else if(curScopeLevel == eScopeLevel){
				
				if(e.getType().equals("Parameter")){
					if(((Parameter)e).getParmode().equals("CV")){
						System.out.println("movi M[" + e.getOffset() + "+R[0]], R[" + r + "]");
					}
					else if(((Parameter)e).getParmode().equals("REF")){
						System.out.println("movi R[255], M[" + e.getOffset() + "+R[0]]");
						System.out.println("movi M[R[255]], R[" + r + "]");
					}
				}
				else if(e.getType().equals("Variable") || e.getType().equals("Temp")){
					System.out.println("movi M[" + e.getOffset() + "+R[0]], R[" + r + "]");
				}
			}
			else if(eScopeLevel < curScopeLevel){
				if(e.getType().equals("Parameter")){
					if(((Parameter)e).getParmode().equals("CV")){
						gnlvcode(v);
						System.out.println("movi M[R[255]], R[" + r + "]");
					}
					else if(((Parameter)e).getParmode().equals("REF")){
						gnlvcode(v);
						System.out.println("movi R[255], M[R[255]]");
						System.out.println("movi M[R[255]], R[" + r + "]");
					}
				}
				else if(e.getType().equals("Variable")){
					gnlvcode(v);
					System.out.println("movi M[R[255]], R[" + r + "]");
				}
			}	
		}
		
		public void relop(String op, String x, String y, String z){
			
			loadvr(x, 1);
			loadvr(y, 2);
			System.out.println("cmpi R[1], R[2]");
			String condjmp;
			if(op.equals("=")) condjmp = "je";
			else if(op.equals("<>")) condjmp = "jne";
			else if(op.equals(">")) condjmp = "jb";
			else if(op.equals(">=")) condjmp = "jbe";
			else if(op.equals("<"))	condjmp = "ja";
			else condjmp = "jae";
			System.out.println(condjmp + " L" + z);	
		}
		
		public void oper(String op, String x, String y, String z){
			
			loadvr(x,1);
			loadvr(y,2);
			String o;
			if(op.equals("+")) o = "addi";
			else if(op.equals("-")) o = "subi";
			else if(op.equals("*")) o = "muli";
			else o = "divi";
			System.out.println(o + " R[3], R[1], R[2]");
			storerv(3,z);
		}
		
		public void parameters(Quad quad){

		
			num_of_pars++;
			int d = tailscope.getFramelength()+12+4*num_of_pars;
			if(quad.getY().equals("CV")){
				loadvr(quad.getX(),255);
				System.out.println("movi M[" + d + "+R[0]], R[255]");
			}
			else if(quad.getY().equals("REF")){
				Entity e = searchEntity(quad.getX());
				int curScopeLevel = tailscope.getNestingLevel();
				int eScopeLevel = e.getNestingLevel();
				
				if(curScopeLevel == eScopeLevel){
					if(e.getType().equals("Parameter")){
						if(((Parameter)e).getParmode().equals("CV")){
							System.out.println("movi R[255],R[0]");
							System.out.println("movi R[254]," + e.getOffset());
							System.out.println("addi R[255],R[254],R[255]");
							System.out.println("movi M[" + d + "+R[0]],R[255]");
						}
						else if(((Parameter)e).getParmode().equals("REF")){
							System.out.println("movi R[255],R[0]");
							System.out.println("movi R[254]," + e.getOffset());
							System.out.println("addi R[255],R[254],R[255]");
							System.out.println("movi R[1],M[R[255]]");
							System.out.println("movi M[" + d + "+R[0]],R[1]");
						}
					}
					else if(e.getType().equals("Variable")){
						System.out.println("movi R[255],R[0]");
						System.out.println("movi R[254]," + e.getOffset());
						System.out.println("addi R[255],R[254],R[255]");
						System.out.println("movi M[" + d + "+R[0]],R[255]");
					}
				}
				else{
					if(e.getType().equals("Parameter")){
						if(((Parameter)e).getParmode().equals("CV")){
							gnlvcode(quad.getX());
							System.out.println("movi M[" + d + "+R[0],R[255]");
						}
						else if(((Parameter)e).getParmode().equals("REF")){
							gnlvcode(quad.getX());
							System.out.println("movi R[1],M[R[255]]");
							System.out.println("movi M[" + d + "+R[0],R[1]");
						}
					}
					else if(e.getType().equals("Variable")){
						gnlvcode(quad.getX());
						System.out.println("movi M[" + d + "+R[0],R[255]");
					}
				}	
			}
			else if(quad.getY().equals("RET")){
				
				Entity e = searchEntity(quad.getX());
	
				System.out.println("movi R[255],R[0]");
				System.out.println("movi R[254]," + e.getOffset());
				System.out.println("addi R[255],R[254],R[255]");
				System.out.println("movi M[" + "R[0]+" + (tailscope.getFramelength()+8) + "],R[255]");
			}
			
		}
		
		public void callFunction(Quad quad){
			
			Entity e = searchEntity(quad.getX());
			
			if(e.getType().equals("Function")){
				int curScopeLevel = tailscope.getNestingLevel();
				int eScopeLevel = e.getNestingLevel();
				
				
				if(curScopeLevel == eScopeLevel){
					System.out.println("movi R[255],M[4+R[0]]");
					System.out.println("movi M[" + (tailscope.getFramelength()+4) + "+R[0]],R[255]");
				}
				else if(eScopeLevel > curScopeLevel){
					System.out.println("movi M[" + (tailscope.getFramelength()+4) + "+R[0]],R[0]");
				}
				
				else{
					System.out.println("ERROR: Function call");
					System.exit(1);
				}
			
				System.out.println("movi R[255]," + (tailscope.getFramelength()+4));
				System.out.println("addi R[0],R[255],R[0]");
				System.out.println("movi R[255], $");
				System.out.println("movi R[254], 15");
				System.out.println("addi R[255],R[255],R[254]");
				System.out.println("movi M[R[0]],R[255]");
				System.out.println("jmp L" + (((Function)e).getStartquad()));
				System.out.println("movi R[255], " + (tailscope.getFramelength()+4));
				System.out.println("subi R[0],R[0],R[255]");
			}
			
		}

		public void generateEndCode(Quad quad){
			
			String op = quad.getOp();
			
			if(quad.getOp().equals("begin_block") && quad.getX().equals(program_name)){
				
				System.out.print("L0: ");
			}
			if(quad.getOp().equals("begin_block") && !quad.getX().equals(program_name)){
				
				System.out.print("L" + quad.getLabel() + ": ");
			}
			
			if(!quad.getOp().equals("begin_block") && !quad.getX().equals(program_name)){
				
				System.out.print("L" + quad.getLabel() + ": ");
			}
			
			if(op.equals("jump")){
				System.out.println("jmp L" + quad.getZ());
			}
			else if(op.equals("=") || op.equals("!=") || op.equals("<>") || op.equals(">") || op.equals(">=") || op.equals("<") || op.equals("<=")){
				relop(quad.getOp(), quad.getX(), quad.getY(), quad.getZ());
			}
			else if(op.equals(":=")){
				loadvr(quad.getX(), 1);
				storerv(1,quad.getZ());
			}
			else if(op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/")){
				oper(quad.getOp(), quad.getX(), quad.getY(), quad.getZ());
			}
			else if(op.equals("out")){
				loadvr(quad.getX(), 1);
				System.out.println("outi R[1]");
			}
			else if(op.equals("retv")){
				loadvr(quad.getX(),1);
				System.out.println("movi R[255],M[R[0]+8]");
				System.out.println("movi M[R[255]],R[1]");
			}
			else if(op.equals("par")){
				parameters(quad);
			}
			else if(op.equals("call")){
				callFunction(quad);
			}
			else if(op.equals("end_block")){
				if(!quad.getX().equals(program_name)) System.out.println("jmp M[R[0]]");
			}
			else if(op.equals("halt")){
				System.out.println("halt");
			}
			
			if(!op.equals("par")) num_of_pars = 0;
		}
	
	public static void main(String args[]) throws IOException{
		
		Compiler c = new Compiler();
		
		c.getWord();
		c.program_stat();
		
		
		//c.printScope();
		//run for 5 tests
		/*
		for(int i = 1; i <= 5; i++){
			c = new Compiler(i);
			mainList.clear();
			error_line = 0;
			temp_line = 0;
			file_name = "2276_test"+i;
			file = new File("C:/test/" + file_name + ".int");
			
			if(!file.exists()) file.createNewFile();
			
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			
			//System.out.println("Test_" + i + " completed");
			c.getWord();
			c.program_stat();	
			
			for(int j =0; j < mainList.size(); j++){
				bw.write(mainList.get(j).getQuad());
				bw.newLine();
			}
			bw.close();
		}*/
		
		
		for(int i =0; i < mainList.size(); i++){
			mainList.get(i).printQuad();
		}
		
	}
}

class Quad{
	
	private int label;
	private String op,x,y,z;
	
	Quad(int label, String op, String x, String y, String z){
		
		this.label = label;
		this.op = op;
		this.x = x;
		this.y = y;
		this.z = z;	
	}
	
	public String getOp(){
		return op;
	}
	
	public String getX(){
		return x;
	}
	
	public String getY(){
		return y;
	}
	
	public String getZ(){
		return z;
	}
	
	public void setX(String s){
		x = s;
	}
	
	public void setY(String s){
		y = s;
	}
	
	public void setZ(String s){
		z = s;
	}
	
	public int getLabel(){
		return label;
	}
	
	
	public String getQuad(){
		return label + " (" + op + "," + x + "," + y + "," + z + ")";
	}
	
	public void printQuad(){
		System.out.println(label + " (" + op + "," + x + "," + y + "," + z + ")");
	}
}

class Variable extends Entity{
	
	String var_type;
	
	Variable(String name, String var_type,int offset,int framelength, int nl){
			
		this.name = name;
		this.var_type = var_type;
		this.offset = offset;
		this.type = "Variable";
		this.framelength = framelength;
		nestinglevel = nl;
	}
	
	public void print(){
		System.out.print("->[" + name + "=" + offset + "]");
	}
}

class Function extends Entity{
	
	private String rtype; //return type
	private int startquad;
	private ArrayList<Argument> arglist;
	
	
	Function(String name, String rtype, ArrayList<Argument> pars, int startquad, int framelength, int nl){
			
		this.name = name;
		this.rtype = rtype;
		arglist = new ArrayList<Argument>();
		this.type = "Function";
		this.startquad = startquad;
		this.framelength = framelength;
		nestinglevel = nl;
		
	}
	
	public ArrayList<Argument> getArgs(){
		return arglist;
	}
	
	
	public void setStartquad(int sq){
		startquad = sq;
	}
	public int getStartquad(){
		return startquad;
	}
	
	public void print(){
	
		System.out.print("->[" + name + "(");
		
		for(int i = 0; i < arglist.size(); i++){
			System.out.print(arglist.get(i).getMode() + ",");
		}
		System.out.print("StartQuad: " + startquad + ")]");
	}
	
}

class Parameter extends Entity{
	
	private String ptype;
	private String parmode = "_";
	
	Parameter(String name, String ptype,int offset, String parmode, int framelength, int nl){
		
		this.name = name;
		this.ptype = ptype;
		this.type = "Parameter";
		this.offset = offset;
		this.parmode = parmode;
		this.framelength = framelength;
		nestinglevel = nl;
	}
	
	public String getParmode(){
		return parmode;
	}
	
	
	public void print(){
		
		System.out.print("->[" + name + "=" + offset + "]");
	}
}

class Temp extends Entity{
	
	Temp(String name, int offset, int framelength, int nl){
		this.name = name;
		this.offset = offset;
		this.type = "Temp";
		this.framelength = framelength;
		nestinglevel = nl;
	}
	
	public void print(){
		
		System.out.print("->[" + name + "=" + offset + "]");
	}
}

abstract class  Entity{

	String name;
	String type;
	int offset;
	int framelength;
	int nestinglevel;
	
	public void setArgs(ArrayList<Argument> args, Function f){
		
	
		ArrayList<Argument> arglist = f.getArgs();
 		for(int i = 0; i < args.size(); i++){
 			arglist.add(args.get(i));
 		}
	}
	
	public String getName(){
		return name;
	}
	
	public String getType(){
		return type;
	}
	
	public int getFramelength(){
		return framelength;
	}
	
	public int getOffset(){
		return offset;
	}
	
	public int getNestingLevel(){
		return nestinglevel;
	}
	
	abstract public void print();

}

class Scope{
	
	private ArrayList<Entity> entities;
	private Scope next;
	private int nestinglevel;
	private int next_offset = 12;

	Scope(int nestinglevel){
		next = null;
		entities = new ArrayList<Entity>();
		this.nestinglevel = nestinglevel;
	}
	
	public void setNext(Scope s){
		next = s;
	}
	
	public Scope getNext(){
		return next;
	}
	
	public int getNextOffset(){
		return next_offset;
	}
	
	public int getFramelength(){
		return next_offset-4;
	}
	
	public void incNextOffset(){
		next_offset+=4;
	}
	
	public int getNestingLevel(){
		return nestinglevel;
	}
	
	public ArrayList<Entity> getEntities(){
		return entities;
	}
	
	public void addEntity(Entity e){
		entities.add(e);
	}
	
	public Entity getEntity(){
		return entities.get(entities.size()-1);
	}
	
	public Scope deleteScope(Scope mainscope){
		
		
		Scope s = mainscope;
		while(s.getNestingLevel() != this.nestinglevel - 1){
			s = s.getNext();
			if(s == null) break;
		}
		if(s != null) s.setNext(null);
		return s;
		
	}
	
	public void print(){
		
		System.out.print("(" + nestinglevel +")");
		for(int i = 0; i < entities.size(); i++){
			entities.get(i).print();
		}
		System.out.println();
		
		Scope s = next;
		
		while(s != null){
			System.out.print("(" + s.getNestingLevel() +")");
			ArrayList<Entity> E = s.getEntities();
			for(int i = 0; i < E.size(); i++){
				E.get(i).print();
			}
			System.out.println();
			s = s.getNext();
		}
	}
}

class Argument{
	
	String mode;
	String type;
	
	Argument(String mode, String type){
		this.mode = mode;
		this.type = type;
	}
	
	public String getMode(){
		return mode;
	}
	
}