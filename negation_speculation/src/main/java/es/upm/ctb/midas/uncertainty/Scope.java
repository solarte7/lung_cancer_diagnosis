package es.upm.ctb.midas.uncertainty;

public class Scope {	
    int begin;
    int end;
    String cue;
    String sentence;
    int sentenceId;
    String filePath;
    String type;
    
    public Scope(){
       begin=0;
       end=0;
       cue="";
       sentence="";
       sentenceId=0;
       filePath="";
       type="";
    }
   
	public String getCue() {
		return cue;
	}

	public void setCue(String cue) {
		this.cue = cue;
	}

	

	public int getSentenceId() {
		return sentenceId;
	}

	public void setSentenceId(int sentenceId) {
		this.sentenceId = sentenceId;
	}

	

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setPhrase(String phrase) {
        this.cue = phrase;
    }

    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }

    public String getPhrase() {
        return cue;
    }
    
    public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	 
    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
   
}
