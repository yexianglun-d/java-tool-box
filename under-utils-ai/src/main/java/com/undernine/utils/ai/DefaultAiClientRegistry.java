package com.undernine.utils.ai;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 默认 AI client 注册表实现。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.3
 */
public final class DefaultAiClientRegistry implements AiClientRegistry {

    private final Map<String, AiClient> clients;
    private final String defaultName;

    /**
     * 创建注册表。
     *
     * @param clients AI client 映射，key 为客户端名称
     * @param defaultName 默认客户端名称
     */
    public DefaultAiClientRegistry(Map<String, ? extends AiClient> clients, String defaultName) {
        Objects.requireNonNull(clients, "clients must not be null");
        if (clients.isEmpty()) {
            throw new IllegalArgumentException("clients must not be empty");
        }
        Map<String, AiClient> normalizedClients = new LinkedHashMap<>();
        clients.forEach((name, client) -> {
            String normalizedName = AiClientRegistry.normalizeName(name);
            if (normalizedClients.containsKey(normalizedName)) {
                throw new IllegalArgumentException("duplicate AI client name: " + normalizedName);
            }
            normalizedClients.put(normalizedName, Objects.requireNonNull(client,
                    "AI client '" + normalizedName + "' must not be null"));
        });
        String normalizedDefaultName = AiClientRegistry.normalizeName(defaultName);
        if (!normalizedClients.containsKey(normalizedDefaultName)) {
            throw new IllegalArgumentException("default AI client does not exist: " + normalizedDefaultName);
        }
        this.clients = Collections.unmodifiableMap(normalizedClients);
        this.defaultName = normalizedDefaultName;
    }

    @Override
    public String getDefaultName() {
        return defaultName;
    }

    @Override
    public AiClient get(String name) {
        String normalizedName = AiClientRegistry.normalizeName(name);
        AiClient client = clients.get(normalizedName);
        if (client == null) {
            throw new NoSuchElementException("AI client does not exist: " + normalizedName);
        }
        return client;
    }

    @Override
    public Optional<AiClient> find(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(clients.get(name.trim()));
    }

    @Override
    public Set<String> names() {
        return clients.keySet();
    }

    @Override
    public String toString() {
        return "DefaultAiClientRegistry{"
                + "defaultName='" + defaultName + '\''
                + ", names=" + clients.keySet()
                + '}';
    }
}
