package es.upm.ctb.midas.uncertainty;

public class CuePosition {
    int begin;
    int end;
    int  negationType;
    boolean isContinuos;
    String negationPhrase;
    int nextPosition;
    int lastPostion;
    int contiguosCuesNumber;
    boolean isMorphological;

    


	public CuePosition(){
        begin=0;
        end=0;
        
        negationType = 0;
        isContinuos = false;
        negationPhrase="";
        nextPosition = -1;
        lastPostion = -1;
        contiguosCuesNumber=0;
        
        isMorphological = false;
        
   }
	
		
	public String getNegationPhrase() {
			return negationPhrase;
	}

	public void setNegationPhrase(String negationPhrase) {
			this.negationPhrase = negationPhrase;
	}

    
    public void setIsContinuos(boolean isContinuos_) {
        this.isContinuos = isContinuos_;       
    }

    public boolean getIsContinuos() {
        return this.isContinuos;
    }
    
    

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setNegationType(int negType) {
        this.negationType = negType;
    }

    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }

    public int getNegationType() {
        return negationType;
    }
    
    	
	
	
    public int getNextPosition() {
		return nextPosition;
	}


	public void setNextPosition(int nextPosition) {
		this.nextPosition = nextPosition;
	}


	public int getLastPostion() {
		return lastPostion;
	}


	public void setLastPostion(int lastPostion) {
		this.lastPostion = lastPostion;
	}

	public int getContiguosCuesNumber() {
		return contiguosCuesNumber;
	}

	public void setContiguosCuesNumber(int contiguosCuesNumber) {
		this.contiguosCuesNumber = contiguosCuesNumber;
	}
	

	   

	public boolean isMorphological() {
		return isMorphological;
	}


	public void setMorphological(boolean isMorphological) {
		this.isMorphological = isMorphological;
	}
		

}//end class
