package Model.Excpetions;

public class NoFileChosenException extends SearcherException {
        public NoFileChosenException(){
            super ("Please fill working and saving path's");
        }
    }
