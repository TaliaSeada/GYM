public class Trainee extends Person {
    private Calender calender;
    private WorkOut workOut;


    public Trainee(String name, int id, int number, double age, double weight, double hight, int gender, int tag, String mail){
        super(name, id, number, age, weight, hight, gender, tag, mail);
        calender = new Calender();
        workOut = new WorkOut();
    }

    public Calender getCalender(){
        return this.calender;
    }
    public void setCalender(Calender c){
        this.calender = c;
    }

    public WorkOut getWorkOut(){
        return this.workOut;
    }
    public void setWorkOut(WorkOut wo){
        this.workOut = wo;
    }


}
