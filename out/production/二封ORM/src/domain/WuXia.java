package domain;

public class WuXia {

    private Integer num;
    private String schoolName;
    private String address;
    private String headmaster;

    public WuXia(){}
    public WuXia(Integer num, String schoolName, String address, String headmaster){
        this.num = num;
        this.schoolName = schoolName;
        this.address = address;
        this.headmaster = headmaster;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHeadmaster() {
        return headmaster;
    }

    public void setHeadmaster(String headmaster) {
        this.headmaster = headmaster;
    }

    public String toString(){
        StringBuffer result = new StringBuffer("[");
        if(this.num != null){
            result.append(this.num);
            result.append(",");
        }
        if(this.schoolName != null){
            result.append(this.schoolName);
            result.append(",");
        }
        if(this.address != null){
            result.append(this.address);
            result.append(",");
        }
        if(this.headmaster != null){
            result.append(this.headmaster);
        }
        result.append("]");
        return result.toString();
    }
}
