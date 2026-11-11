package lirihiwa.dev.ai.stt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.io.InputStream;
import java.time.Duration;

@Component
public class LocalSpeechRecognitionClient implements SpeechRecognitionClient {

    private final RestClient restClient;

    public LocalSpeechRecognitionClient(@Value("${stt.service-url}") String serviceUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setReadTimeout(Duration.ofMinutes(30));
        this.restClient = RestClient.builder()
                .baseUrl(serviceUrl)
                .requestFactory(factory)
                .build();
    }

    @Override
    public TranscriptionResult transcribe(InputStream audio, String fileName) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new InputStreamResource(audio) {
            @Override
            public String getFilename() {
                return fileName;
            }

            @Override
            public long contentLength() {
                return -1;
            }
        });

        return restClient.post()
                .uri("/transcribe")
                .body(body)
                .retrieve()
                .body(TranscriptionResult.class);
    }
}
