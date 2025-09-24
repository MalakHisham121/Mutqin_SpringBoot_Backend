package org.example.mutqinbackend.DTO;

public class CalendlyResponse<T> {
    private T resource;

    public T getResource() { return resource; }
    public void setResource(T resource) { this.resource = resource; }
}
