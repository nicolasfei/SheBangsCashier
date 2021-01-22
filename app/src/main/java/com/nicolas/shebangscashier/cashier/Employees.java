package com.nicolas.shebangscashier.cashier;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 员工，导购
 */
public class Employees {
    public String id = "";             //111",
    public String b_b_Branch_Id = "";  //0220",
    public String mark = "";           //导购",
    public String name = "";           //沈勇",
    public String sex = "";            //男",
    public String idCard = "";         //510183",
    public String idCardP = "";        //1",
    public String idCardN = "";        //1",
    public String realImg = "";        //1",
    public String c_b_Area_Id = "";    //510183",
    public String address = "";        //邛崃市关家村12组",
    public String tel = "";            //18224407848",
    public String password = "";       //1",
    public String urgentName = "";     //1",
    public String urgentTel = "";      //1",
    public String c_b_Bank_Id = "";    //ICBC",
    public String bankNo = "";         //6212264402004467183",
    public int baseWage = 0;          //2000,
    public String commission = "";     //0.2",
    public String inTime = "";         //2020-08-05T19:03:43",
    public String leaveTime = "";      //2020-08-05T19:03:46",
    public String state = "";          //working",
    public String addTime = "";        //2020-08-05T19:03:55",
    public String remark = "";         //1",
    public String valid = "";          //启用"

    public Employees(){

    }

    public Employees(String json) {
        try {
            JSONObject object = new JSONObject(json);
            this.id = object.getString("id");
            this.b_b_Branch_Id = object.getString("b_b_Branch_Id");
            this.mark = object.getString("mark");
            this.name = object.getString("name");
            this.sex = object.getString("sex");

            this.idCard = object.getString("idCard");
            this.idCardP = object.getString("idCardP");
            this.idCardN = object.getString("idCardN");
            this.realImg = object.getString("realImg");
            this.c_b_Area_Id = object.getString("c_b_Area_Id");

            this.address = object.getString("address");
            this.tel = object.getString("tel");
            this.password = object.getString("password");
            this.urgentName = object.getString("urgentName");
            this.urgentTel = object.getString("urgentTel");

            this.c_b_Bank_Id = object.getString("c_b_Bank_Id");
            this.bankNo = object.getString("bankNo");
            this.baseWage = object.getInt("baseWage");
            this.commission = object.getString("commission");
            this.inTime = object.getString("inTime");

            this.leaveTime = object.getString("leaveTime");
            this.state = object.getString("state");
            this.addTime = object.getString("addTime");
            this.remark = object.getString("remark");
            this.valid = object.getString("valid");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
