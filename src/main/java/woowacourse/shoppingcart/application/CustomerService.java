package woowacourse.shoppingcart.application;

import org.springframework.stereotype.Service;
import woowacourse.auth.dto.TokenRequest;
import woowacourse.shoppingcart.domain.Customer;
import woowacourse.shoppingcart.domain.Password;
import woowacourse.shoppingcart.dto.CustomerLoginRequest;
import woowacourse.shoppingcart.dto.CustomerLoginResponse;
import woowacourse.shoppingcart.dto.CustomerRequest;
import woowacourse.shoppingcart.dto.CustomerResponse;
import woowacourse.shoppingcart.dto.CustomerUpdateRequest;
import woowacourse.shoppingcart.dto.PasswordRequest;
import woowacourse.shoppingcart.repository.CustomerRepository;

@Service
public class CustomerService {

    private CustomerRepository customerRepository;

    public CustomerService(final CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Long signUp(final CustomerRequest request) {
        return customerRepository.create(request.toInitCustomer());
    }

    public CustomerLoginResponse login(final CustomerLoginRequest request) {
        Customer customer = customerRepository.login(request.getUserId(), request.getPassword());
        return CustomerLoginResponse.ofExceptToken(customer);
    }

    public CustomerResponse findById(final TokenRequest request) {
        Customer customer = customerRepository.findById(request.getId());
        return CustomerResponse.of(customer);
    }

    public void update(final TokenRequest tokenRequest, final CustomerUpdateRequest customerUpdateRequest) {
        Customer oldCustomer = customerRepository.findById(tokenRequest.getId());
        Customer newCustomer = customerUpdateRequest.updatedCustomer(oldCustomer);
        customerRepository.update(newCustomer);
    }

    public void updatePassword(final TokenRequest tokenRequest, final PasswordRequest passwordRequest) {
        Password oldPassword = new Password(passwordRequest.getOldPassword());
        Password newPassword = new Password(passwordRequest.getNewPassword());
        customerRepository.updatePassword(tokenRequest.getId(), oldPassword, newPassword);
    }

    public void withdraw(final TokenRequest request) {
        customerRepository.delete(request.getId());
    }
}
