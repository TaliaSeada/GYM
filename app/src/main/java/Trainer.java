public class Trainer  extends Person {
    private Calender calender;
    private double payment;
    private SearchBar sb;


    public Trainer(String name, int id, int number, double age, double weight, double hight, int gender, int tag, String mail, double payment){
        super(name, id, number, age, weight, hight, gender, tag, mail);
        this.calender = new Calender();
        this.sb = new SearchBar();
        this.payment = payment;
    }

    public Calender getCalender(){
        return this.calender;
    }
    public void setCalender(Calender c){
        this.calender = c;
    }

    public double getPayment(){
        return this.payment;
    }
    public void setPayment(double payment){
        this.payment = payment;
    }
}
