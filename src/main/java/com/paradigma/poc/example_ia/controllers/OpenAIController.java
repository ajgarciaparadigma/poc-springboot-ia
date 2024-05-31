package com.paradigma.poc.example_ia.controllers;


import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.audio.speech.SpeechMessage;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Base64;
import java.util.Map;


@RestController
public class OpenAIController {

    private final OpenAiChatModel chatModel;

    private final OpenAiImageModel imageModel;

    private final OpenAiAudioSpeechModel audioSpeechModel;

    @Autowired
    public OpenAIController(OpenAiChatModel chatModel, OpenAiImageModel imageModel, OpenAiAudioSpeechModel audioSpeechModel) {
        this.chatModel = chatModel;
        this.imageModel = imageModel;
        this.audioSpeechModel = audioSpeechModel;
    }

    @GetMapping("/ai/generate")
    public Map generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("generation", chatModel.call(message));
    }

    @GetMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatModel.stream(prompt);
    }


    @GetMapping(value = "/ai/generateImage", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        //
        ImagePrompt prompt = new ImagePrompt(message, OpenAiImageOptions.builder().withResponseFormat("b64_json").build());

        ImageResponse imageResponse =  imageModel.call(prompt);

        String base64Image = imageResponse.getResults().get(0).getOutput().getB64Json();
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }


    @GetMapping(value = "/ai/generateAudio")
    public ResponseEntity<byte[]> getAudio(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {

        String joke =  chatModel.call(message);
        SpeechPrompt speechPrompt = new SpeechPrompt(new SpeechMessage(joke));

        String format = "audio/wav";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(format));

        SpeechResponse response = audioSpeechModel.call(speechPrompt);

        return new ResponseEntity<>(response.getResult().getOutput(), headers, HttpStatus.OK);
    }

}