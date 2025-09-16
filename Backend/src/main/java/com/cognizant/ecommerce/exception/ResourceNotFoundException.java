package com.cognizant.ecommerce.exception;

<<<<<<< HEAD
=======

>>>>>>> d894a8dc0c82e3b5bfd10bfc2ec9ee207806ca54
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
<<<<<<< HEAD
}
=======
}

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

>>>>>>> d894a8dc0c82e3b5bfd10bfc2ec9ee207806ca54
