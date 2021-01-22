package com.nicolas.shebangscashier.communication.login;

import com.nicolas.shebangscashier.communication.Command;
import com.nicolas.shebangscashier.communication.CommandTypeEnum;
import com.nicolas.shebangscashier.communication.CommandVo;

public class LoginCommand extends Command {

    @Override
    public String execute(CommandVo vo) {
        return super.firstNode.handleMessage(vo);
    }

    @Override
    protected void buildDutyChain() {
        LoginInterface login = new Login();
        LoginInterface logout = new Logout();
        LoginInterface queryEmployees = new QueryEmployees();

        login.setNextHandler(logout);
        logout.setNextHandler(queryEmployees);
        queryEmployees.setNextHandler(null);
        super.firstNode = login;
    }

    @Override
    public CommandTypeEnum getCommandType() {
        return CommandTypeEnum.COMMAND_LOGIN;
    }
}
