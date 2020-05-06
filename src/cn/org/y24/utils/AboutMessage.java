package cn.org.y24.utils;

public class AboutMessage {
    private final String author;
    private final String version;
    private final String GitHub;
    private final String DevEnv;
    private final String introduction;

    public AboutMessage(String author, String version, String GitHub, String DevEnv, String introduction) {
        this.author = author;
        this.version = version;
        this.GitHub = GitHub;
        this.DevEnv = DevEnv;
        this.introduction = introduction;
    }

    @Override
    public String toString() {
        return
                " Author: '" + author + '\'' + '\n' +
                        " Version: '" + version + '\'' + '\n' +
                        " GitHub: '" + GitHub + '\'' + '\n' +
                        " DevEnv: '" + DevEnv + '\'' + '\n' +
                        " Introduction: '" + introduction + '\'' + '\n';
    }
}