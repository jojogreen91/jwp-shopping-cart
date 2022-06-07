package woowacourse.shoppingcart.repository.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import woowacourse.shoppingcart.domain.Customer;
import woowacourse.shoppingcart.exception.InvalidCustomerException;
import woowacourse.shoppingcart.exception.InvalidLoginException;
import woowacourse.shoppingcart.exception.InvalidPasswordException;
import woowacourse.shoppingcart.exception.ResourceNotFoundException;

@Repository
public class CustomerDao {

    private static final String REAL_CUSTOMER_QUERY = " (select id, username, password, nickname from customer where withdrawal = false) ";
    private static final RowMapper<Customer> ROW_MAPPER = (resultSet, rowNum) -> new Customer(
            resultSet.getLong("id"),
            resultSet.getString("username"),
            resultSet.getString("password"),
            resultSet.getString("nickname")
    );

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public CustomerDao(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Long findIdByUserName(final String username) {
        try {
            final String query = "SELECT id FROM customer WHERE username = :username";
            Map<String, Object> params = new HashMap<>();
            params.put("username", username);
            return namedParameterJdbcTemplate.queryForObject(query, params, Long.class);
        } catch (final EmptyResultDataAccessException e) {
            throw new InvalidCustomerException("존재하지 않는 유저입니다.");
        }
    }

    // new method

    public Long create(final Customer customer) {
        String query = "insert into customer (username, password, nickname, withdrawal)"
                + " values (:username, :password, :nickname, false)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource source = new BeanPropertySqlParameterSource(customer);
        namedParameterJdbcTemplate.update(query, source, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public Customer findById(final Long id) {
        String query = "select id, username, password, nickname from"
                + REAL_CUSTOMER_QUERY
                + "where id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        try {
            return namedParameterJdbcTemplate.queryForObject(query, params, ROW_MAPPER);
        } catch (EmptyResultDataAccessException exception) {
            throw new ResourceNotFoundException("존재하지 않는 회원입니다.");
        }
    }

    public Customer login(final String username, final String password) {
        String query = "select id, username, password, nickname from"
                + REAL_CUSTOMER_QUERY
                + "where username = :username and password = :password";
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        try {
            return namedParameterJdbcTemplate.queryForObject(query, params, ROW_MAPPER);
        } catch (EmptyResultDataAccessException exception) {
            throw new InvalidLoginException("로그인 할 수 없습니다.");
        }
    }

    public void update(final Customer newCustomer) {
        String query = "update customer set nickname = :nickname where id = :id and exists" + REAL_CUSTOMER_QUERY;
        Map<String, Object> params = new HashMap<>();
        params.put("id", newCustomer.getId());
        params.put("nickname", newCustomer.getNickname());
        int affectedRowCount = namedParameterJdbcTemplate.update(query, params);
        if (affectedRowCount == 0) {
            throw new ResourceNotFoundException("존재하지 않는 회원입니다.");
        }
    }

    public void updatePassword(final Long id, final String oldPassword, final String newPassword) {
        String query = "update customer set password = :newPassword"
                + " where id = :id and password = :oldPassword and exists" + REAL_CUSTOMER_QUERY;
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("newPassword", newPassword);
        params.put("oldPassword", oldPassword);
        int affectedRowCount = namedParameterJdbcTemplate.update(query, params);
        if (affectedRowCount == 0) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }
    }

    public void delete(final Long id) {
        String query = "update customer set withdrawal = true where id = :id and withdrawal = false";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        int affectedRowCount = namedParameterJdbcTemplate.update(query, params);
        if (affectedRowCount == 0) {
            throw new ResourceNotFoundException("존재하지 않는 회원입니다.");
        }
    }

    public boolean checkDuplicatedUsername(final String username) {
        String query = "select EXISTS (select id from customer where username = :username)";
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject(query, params, Boolean.class));
    }

    public boolean checkDuplicatedNickname(final String nickname) {
        String query = "select EXISTS (select id from customer where nickname = :nickname)";
        Map<String, Object> params = new HashMap<>();
        params.put("nickname", nickname);
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject(query, params, Boolean.class));
    }

    public boolean matchPassword(final Long id, final String password) {
        String query = "select EXISTS (select id from"
                + REAL_CUSTOMER_QUERY
                + "where id = :id and password = :password)";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("password", password);
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject(query, params, Boolean.class));
    }
}
