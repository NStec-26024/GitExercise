package numer0n.service.impl;

import static numer0n.constant.GameConfig.*;
import static numer0n.constant.MessageConfig.*;
import numer0n.factory.service.NumeronCommandHandlerFactory;
import numer0n.factory.util.InputCommandFactory;
import numer0n.service.CommandHandler;
import numer0n.service.NumeronManager;
import numer0n.service.NumeronResult;
import numer0n.util.InputCommand;
import numer0n.util.NumeronNumberGenerator;

public class NumeronManagerImpl implements NumeronManager {
    private InputCommand inputCommand;

    private CommandHandler commandHandler;

    private int remainingTurn;

    private String numeronNumber;

    private boolean isGameOver;

    private boolean isGameClear;

    public boolean isGameClear() {
        return isGameClear;
    }

    public NumeronManagerImpl() {
        this.inputCommand = InputCommandFactory.create();

        this.commandHandler = NumeronCommandHandlerFactory.create();
    }

    @Override
    public void start() {
        // ヌメロンのゲーム状態を初期化
        this.initialize();

        // ゲーム開始
        this.run();
    }

    /**
     * ゲーム実行メソッド
     */
    private void run() {
        System.out.println(GAME_START_MESSAGE);

        while (true) {
            System.out.println(REMAINING_TURN_MESSAGE + this.remainingTurn);
            this.inputCommand.input(INPUT_NUMERON_ACTION_MESSAGE);

            String input = this.inputCommand.getText();

            try {
                String command = this.commandHandler.handle(input);

                if (this.executeCommand(command)) {
                    break;
                }

                if (this.isGameOver) {
                    System.out.println(GAME_OVER_MESSAGE);
                    break;
                }
            }
            // コマンドに存在しない場合
            catch (Exception e) {
                System.out.println(INVALID_INPUT_NUMERON_COMMAND_MESSAGE);
                continue;
            }
        }
    }

    /**
     * ゲーム初期化メソッド
     */
    private void initialize() {
        this.remainingTurn = MAX_TURN;
        this.isGameOver = false;
        this.isGameClear = false;

        // ヌメロンの数字を生成
        this.generateNumeronNumber();

    }

    /**
     * ヌメロンの数字を生成するメソッド
     */
    private void generateNumeronNumber() {
        this.numeronNumber = NumeronNumberGenerator.generateUniqueDigitNumber();
    }

    private boolean executeCommand(String command) {
        switch (command) {
            // Give up
            case GIVE_UP:
                this.isGameOver = true;
                return false;

            // Item: Slash
            case USE_ITEM:
                this.next();
                return false;

            // 回答入力
            case ANSWER:
                NumeronResult result =
                        new NumeronResultImpl(this.numeronNumber, this.inputCommand.getText());

                if (result.isClear()) {
                    this.gameClear();
                    return true;
                }

                System.out.println(result.getEat() + " EAT : " + result.getBite() + " BITE");

                this.next();

                return false;

            default:
                System.out.println("default");
                return false;
        }
    }

    private void next() {
        if (--this.remainingTurn < 1) {
            this.isGameOver = true;
        }
    }

    private void gameClear() {
        this.isGameClear = true;

        System.out.println(GAME_CLEAR_MESSAGE);
    }
}
