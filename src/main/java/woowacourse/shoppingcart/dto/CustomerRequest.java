package woowacourse.shoppingcart.dto;

public class CustomerRequest {

    private String userId;
    private String nickname;
    private String password;

    public CustomerRequest() {
    }

    public CustomerRequest(final String userId, final String nickname, final String password) {
        this.userId = userId;
        this.nickname = nickname;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }
}