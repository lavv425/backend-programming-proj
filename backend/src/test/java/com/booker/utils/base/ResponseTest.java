package com.booker.utils.base;

import com.booker.constants.ErrorCodes;
import com.booker.constants.SuccessCodes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResponseTest {

    @Test
    void constructor_shouldCreateSuccessResponse() {
        String data = "test data";
        Response<String> response = new Response<>(true, data, SuccessCodes.OK);

        assertTrue(response.status);
        assertEquals(data, response.data);
        assertEquals(SuccessCodes.OK, response.message);
    }

    @Test
    void constructor_shouldCreateErrorResponse() {
        Response<String> response = new Response<>(false, null, ErrorCodes.USER_NOT_FOUND);

        assertFalse(response.status);
        assertNull(response.data);
        assertEquals(ErrorCodes.USER_NOT_FOUND, response.message);
    }

    @Test
    void constructor_shouldInitializeAllFields() {
        String data = "test";
        Response<String> response = new Response<>(true, data, SuccessCodes.OK);

        assertTrue(response.status);
        assertEquals(data, response.data);
        assertEquals(SuccessCodes.OK, response.message);
    }

    @Test
    void equals_withSameContent_shouldReturnTrue() {
        Response<String> response1 = new Response<>(true, "data", SuccessCodes.OK);
        Response<String> response2 = new Response<>(true, "data", SuccessCodes.OK);

        assertEquals(response1, response2);
    }

    @Test
    void equals_withDifferentContent_shouldReturnFalse() {
        Response<String> response1 = new Response<>(true, "data1", SuccessCodes.OK);
        Response<String> response2 = new Response<>(true, "data2", SuccessCodes.OK);

        assertNotEquals(response1, response2);
    }

    @Test
    void hashCode_withSameContent_shouldBeEqual() {
        Response<String> response1 = new Response<>(true, "data", SuccessCodes.OK);
        Response<String> response2 = new Response<>(true, "data", SuccessCodes.OK);

        assertEquals(response1.hashCode(), response2.hashCode());
    }
}
