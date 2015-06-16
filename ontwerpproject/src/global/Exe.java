package global;

public class Exe {
	public static void main(String[] args) {
		if(args.length>1){
			if(args[1].equals("gui")){
				Globals.GUI=true;
			}else{
				Globals.GUI=false;
			}
		}
		
		if(Globals.GUI){
			//TODO start met gui
		}else{
			//TODO start zonder
		}
	}
}
