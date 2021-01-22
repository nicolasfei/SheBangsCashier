package com.nicolas.shebangscashier.cashier;

import android.text.TextUtils;

import com.nicolas.componentlibrary.multileveltree.TreeNode;
import com.nicolas.shebangscashier.communication.CommandTypeEnum;
import com.nicolas.shebangscashier.communication.CommandVo;
import com.nicolas.shebangscashier.communication.Invoker;
import com.nicolas.shebangscashier.communication.common.CommonInterface;
import com.nicolas.shebangscashier.communication.login.LoginInterface;
import com.nicolas.toollibrary.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MyKeeper {

    private static final MyKeeper keeper = new MyKeeper();

    private String loginName;               //登陆用户名
    private String loginPassword;           //登陆密码
    private String token;                   //登陆成功后返回key

    private List<Employees> employees;      //导购人员列表
    private String[] employeesName;         //导购人员列表
    private Employees staff;                //在岗导购
    private Branch branch;                  //分店信息
    private TreeNode goodsClassTree;        //货物类型树

    private InformationObserveTask timerTask;       //信息定时查询任务

    private MyKeeper() {
        this.staff = new Employees();
        this.employees = new ArrayList<>();
        this.timerTask = new InformationObserveTask();
    }

    /**
     * 设置登陆信息
     *
     * @param userName 用户名
     * @param password 密码
     */
    public void setLogin(String userName, String password) {
        this.loginName = userName;
        this.loginPassword = password;
    }

    /**
     * 获取登陆名
     *
     * @return 登录名
     */
    public String getLoginName() {
        return loginName;
    }

    /**
     * 获取登陆密码
     *
     * @return 登陆密码
     */
    public String getLoginPassword() {
        return loginPassword;
    }

    /**
     * 单例接口
     *
     * @return MyKeeper
     */
    public static MyKeeper getInstance() {
        return keeper;
    }

    /**
     * 设置分店信息
     *
     * @param branch json
     */
    public void setBranch(String branch) {
        this.branch = new Branch(branch);
    }

    /**
     * 获取分店
     *
     * @return 分店信息
     */
    public Branch getBranch() {
        return branch;
    }

    /**
     * 获取商品类型tree
     *
     * @return tree
     */
    public TreeNode getGoodsClassTree() {
        synchronized (MyKeeper.class) {
            return goodsClassTree;
        }
    }

    /**
     * 清空商品类型tree选择
     */
    public void clearGoodsClassSelect() {
        this.goodsClassTree.setSelectStatus(TreeNode.NO_SELECT);
    }

    /**
     * 设置在岗导购
     *
     * @param name 导购名
     */
    public void setStaff(String name) {
        for (Employees item : employees) {
            if (item.name.equals(name)) {
                this.staff = item;
                break;
            }
        }
    }

    /**
     * 获取在岗导购
     *
     * @return 在岗导购
     */
    public Employees getStaff() {
        return staff;
    }

    /**
     * 开始定时查询任务
     */
    public void startTimerTask() {
        this.timerTask.start();
    }

    /**
     * 退出定时查询任务
     */
    public void cancelTimerTask() {
        this.timerTask.cancel();
    }

    /**
     * 设置导购人员
     *
     * @param json json
     */
    public void setEmployees(String json) throws JSONException {
        List<Employees> list = new ArrayList<>();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            Employees item = new Employees(array.getString(i));
            list.add(item);
        }
        synchronized (MyKeeper.class) {
            employees.clear();
            employees.addAll(list);
            employeesName = new String[employees.size()];
            for (int i = 0; i < employees.size(); i++) {
                employeesName[i] = employees.get(i).name;
            }
        }
    }

    /**
     * 获取所有导购
     *
     * @return 所有导购
     */
    public List<Employees> getEmployees() {
        return employees;
    }

    /**
     * 获取所有导购姓名列表
     *
     * @return 所有导购姓名列表
     */
    public String[] getEmployeesName() {
        return employeesName;
    }

    /**
     * 设置token
     *
     * @param token token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 获取token
     *
     * @return token
     */
    public String getToken() {
        return token;
    }

    /**
     * 支付方式
     *
     * @return 支付方式
     */
    public String[] getPayment() {
        return new String[]{"支付宝", "微信", "现金", "刷卡"};
    }

    /**
     * 查询库房，分店信息数据类
     */
    private class InformationObserveTask {
        private long PERIOD = 300 * 60 * 1000;       //30分钟执行一次
        private Timer timer;

        private void start() {
            this.timer = new Timer();
            this.timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //查询导购
                    queryEmployees();
                    //查询商品分类
                    queryGoodsType("");
                }
            }, 0, PERIOD);
        }

        /**
         * 查询导购
         */
        private void queryEmployees() {
            //查询导购
            CommandVo vo = new CommandVo();
            vo.typeEnum = CommandTypeEnum.COMMAND_LOGIN;
            vo.url = LoginInterface.QueryEmployees;
            vo.contentType = HttpHandler.ContentType_APP;
            vo.requestMode = HttpHandler.RequestMode_POST;
            Map<String, String> parameters = new HashMap<>();
            vo.parameters = parameters;
            String employeesMsg = Invoker.getInstance().synchronousExec(vo);

            try {
                JSONObject object = new JSONObject(employeesMsg);
                if (object.getBoolean("success")) {
                    if (object.has("data")) {
                        List<Employees> list = new ArrayList<>();
                        JSONArray array = object.getJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {
                            Employees item = new Employees(array.getString(i));
                            list.add(item);
                        }
                        synchronized (MyKeeper.class) {
                            employees.clear();
                            employees.addAll(list);
                            employeesName = new String[employees.size()];
                            for (int i = 0; i < employees.size(); i++) {
                                employeesName[i] = employees.get(i).name;
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void queryGoodsType(String name) {
            //查询商品分类
            CommandVo goodsVo = new CommandVo();
            goodsVo.typeEnum = CommandTypeEnum.COMMAND_COMMON;
            goodsVo.url = CommonInterface.GoodsClass;
            goodsVo.contentType = HttpHandler.ContentType_APP;
            goodsVo.requestMode = HttpHandler.RequestMode_POST;
            Map<String, String> parameters = new HashMap<>();
            if (!TextUtils.isEmpty(name)) {
                parameters.put("name", name);           //选填
            }
            goodsVo.parameters = parameters;
            String goodClass = Invoker.getInstance().synchronousExec(goodsVo);
            TreeNode goodsTree;
            try {
                JSONObject object = new JSONObject(goodClass);
                if (object.getBoolean("success")) {
                    if (object.has("jsonData")) {
                        goodsTree = TreeNode.buildTree(object.getString("jsonData"));
                        synchronized (MyKeeper.class) {
                            goodsClassTree = goodsTree;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void cancel() {
            this.timer.cancel();
        }
    }
}
