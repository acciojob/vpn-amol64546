package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{
        CountryName countryName1=null;
        for(CountryName c: CountryName.values()){
            if(c.name().equalsIgnoreCase(countryName)){
                countryName1 = c;
                break;
            }
        }
        if(countryName1==null){
            throw new Exception("Country not found");
        }

        User user = new User(username,password);

        Country country = new Country();

        country.setCountryName(countryName1);
        country.setCode(countryName1.toCode());


        user.setConnected(false);
        user.setOriginalCountry(country);
        user.setOriginalIp(country.getCode()+"."+user.getId());

        country.setUser(user);
        userRepository3.save(user);

        return user;
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        ServiceProvider serviceProvider = serviceProviderRepository3.findById(serviceProviderId).get();
        User user = userRepository3.findById(userId).get();

        user.getServiceProviderList().add(serviceProvider);
        serviceProvider.getUsers().add(user);
        userRepository3.save(user);
        return user;
    }
}
