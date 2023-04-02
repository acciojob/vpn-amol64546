package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;





    @Override
    public User connect(int userId, String countryName) throws Exception{
        User user = userRepository2.findById(userId).get();
        if(user.getConnected()){
            throw new Exception("Already connected");
        }

        if(user.getOriginalCountry().getCountryName().name().equalsIgnoreCase(countryName)){
            return user;
        }

        Country country=null;
        int minId = Integer.MAX_VALUE;
        ServiceProvider serviceProvider1=null;

        for(ServiceProvider s: user.getServiceProviderList()){
            for(Country c: s.getCountryList())
                if(c.getCountryName().name().equalsIgnoreCase(countryName) && minId>c.getId()){
                    country = c;
                    minId = c.getId();
                    serviceProvider1 = s;
                }
        }

        if(minId == Integer.MAX_VALUE){
            throw new Exception("Unable to connect");
        }


        Connection connection = new Connection(user);
        connection.setServiceProvider(serviceProvider1);

        serviceProvider1.getConnectionList().add(connection);

        user.setConnected(true);
        user.getConnectionList().add(connection);
        user.setMaskedIp( country.getCode() +"."+serviceProvider1.getId()+"."+user.getId());

        serviceProviderRepository2.save(serviceProvider1);
        return userRepository2.save(user);
    }
    @Override
    public User disconnect(int userId) throws Exception {

        User user = userRepository2.findById(userId).get();
        if(!user.getConnected()){
            throw new Exception("Already disconnected");
        }
        user.setMaskedIp(null);
        user.setConnected(false);
        return userRepository2.save(user);

    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        User sender = userRepository2.findById(senderId).get();
        User receiver = userRepository2.findById(receiverId).get();

        String receiverCountryName = "";

        if(receiver.getConnected()){
            String receiverCountryCode = receiver.getMaskedIp().substring(0,3);

            CountryName countryName1=null;
            for(CountryName c: CountryName.values()){
                if(c.toCode().equals(receiverCountryCode)){
                    receiverCountryName = c.name();
                    break;
                }
            }


        }else{
             receiverCountryName = receiver.getOriginalCountry().getCountryName().toString();
        }

        if(sender.getOriginalCountry().equals(receiver.getOriginalCountry())){
            return sender;
        }

        try{
            sender = connect(sender.getId(),receiverCountryName);
        }catch (Exception e){
            throw new Exception("Cannot establish communication");
        }

        return sender;
    }
}
