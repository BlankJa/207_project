package placefinder.usecases.ports;

import placefinder.entities.User;

public interface UserGateway {
    User findByEmail(String email) throws Exception;
    User findById(int id) throws Exception;
    void save(User user) throws Exception;
}
