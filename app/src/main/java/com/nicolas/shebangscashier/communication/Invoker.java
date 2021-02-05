package com.nicolas.shebangscashier.communication;

import android.os.Handler;

import com.nicolas.shebangscashier.communication.common.CommonCommand;
import com.nicolas.shebangscashier.communication.manage.ManageCommand;
import com.nicolas.shebangscashier.communication.sale.SaleCommand;
import com.nicolas.shebangscashier.communication.vip.VipCommand;
import com.nicolas.shebangscashier.communication.login.LoginCommand;

import java.util.ArrayList;
import java.util.List;

public class Invoker {

    private OnExecResultCallback callback;
    private static Invoker invoker = new Invoker();
    private List<Command> commandList;

    private Invoker() {
        //创建所需命令
        commandList = new ArrayList<>();
        commandList.add(new CommonCommand());
        commandList.add(new LoginCommand());
        commandList.add(new SaleCommand());
        commandList.add(new VipCommand());
        commandList.add(new ManageCommand());
    }

    public static Invoker getInstance() {
        return invoker;
    }

    public void exec(CommandVo vo) {
        //线程异步执行
        TaskThread thread = new TaskThread(vo);
        thread.start();
    }

    /**
     * 同步执行
     *
     * @param vo 命令
     */
    public String synchronousExec(CommandVo vo) {
        for (Command c : commandList) {
            if (c.getCommandType() == vo.typeEnum) {
                return c.execute(vo);
            }
        }
        return null;
    }

    //设置处理结果回调接口
    public void setOnEchoResultCallback(OnExecResultCallback callback) {
        this.callback = callback;
    }

    //处理结果返回接口
    public interface OnExecResultCallback {
        void execResult(CommandResponse result);
    }

    /**
     * handler
     */
    private static Handler handler = new Handler();

    /**
     * 线程执行
     */
    private class TaskThread extends Thread {
        private CommandVo vo;

        private TaskThread(CommandVo vo) {
            this.vo = vo;
        }

        @Override
        public void run() {
            for (Command c : commandList) {
                if (c.getCommandType() == vo.typeEnum) {
                    final String result = c.execute(vo);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.execResult(new CommandResponse(result, vo.url));
                        }
                    });
                    break;
                }
            }
        }
    }
}
