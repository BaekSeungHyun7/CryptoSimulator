package com.baeksh.cryptoSimulator.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

public class CustomSerializer {
  

  //BigDecimal 값을 소수점 형식의 문자열로 변환하여 직렬화
  public static class BigDecimalPlainStringSerializer extends JsonSerializer<BigDecimal> {
    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      gen.writeString(value.stripTrailingZeros().toPlainString());
      
    }
  }
  
  
  
}

/*
 * https://www.baeldung.com/jackson-custom-serialization
 * https://devfunny.tistory.com/861
 */