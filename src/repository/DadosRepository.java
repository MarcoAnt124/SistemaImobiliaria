package repository;

import model.*;

import java.nio.file.AtomicMoveNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DadosRepository {
    private static final String ARQUIVO_DADOS = "dados_imobiliaria.json";

    private ArrayList<Vendedor> listaVendedores;
    private ArrayList<Edificio> listaEdificios;
    private ArrayList<Cliente> listaClientes;
    private ArrayList<Venda> listaVendas;

    public DadosRepository() {
        this.listaVendedores = new ArrayList<>();
        this.listaEdificios = new ArrayList<>();
        this.listaClientes = new ArrayList<>();
        this.listaVendas = new ArrayList<>();
        lerArquivo();
    }

    public ArrayList<Cliente> getListaClientes() {
        return listaClientes;
    }

    public void setListaClientes(ArrayList<Cliente> listaClientes) {
        this.listaClientes = listaClientes;
    }

    public ArrayList<Venda> getListaVendas() {
        return listaVendas;
    }

    public void setListaVendas(ArrayList<Venda> listaVendas) {
        this.listaVendas = listaVendas;
    }

    public ArrayList<Vendedor> listaVendedores(){
        return this.listaVendedores;
    }

    public ArrayList<Edificio> listaEdificios(){
        return this.listaEdificios;
    }

    public ArrayList<Vendedor> getListaVendedores() {
        return this.listaVendedores;
    }

    public ArrayList<Edificio> getListaEdificio() {
        return this.listaEdificios;
    }

    public boolean anexarVendedor(Vendedor vendedor) {
        if (vendedor == null) {
            return false;
        }

        this.listaVendedores.add(vendedor);
        return gravarArquivo();
    }

    public boolean anexarEdificio(Edificio edificio) {
        if (edificio == null) {
            return false;
        }

        this.listaEdificios.add(edificio);
        return gravarArquivo();
    }

    public boolean anexarCliente(Cliente cliente){
        //Só o esqueleto para teste
        return false;
    }

    public boolean anexarVenda(Venda venda){
        //Só o esqueleto para teste
        return false;
    }

    public boolean gravarArquivo() {
        try {
            String json = gerarJson();
            Path caminho = Paths.get(ARQUIVO_DADOS);
            Files.writeString(caminho, json, StandardCharsets.UTF_8);
            return true;
        } catch (Exception e) {
            System.out.println("[ERRO] Falha ao gravar arquivo JSON: " + e.getMessage());
            return false;
        }
    }

    public boolean lerArquivo() {
        Path caminho = Paths.get(ARQUIVO_DADOS);
        if (!Files.exists(caminho)) {
            return true;
        }

        try {
            String json = Files.readString(caminho, StandardCharsets.UTF_8).trim();
            if (json.isEmpty()) {
                this.listaVendedores = new ArrayList<>();
                this.listaEdificios = new ArrayList<>();
                return true;
            }

            JsonParser parser = new JsonParser(json);
            JsonValue raiz = parser.parse();

            if (!(raiz instanceof JsonObject)) {
                throw new IllegalArgumentException("JSON raiz invalido");
            }

            JsonObject objRaiz = (JsonObject) raiz;

            this.listaVendedores = parseVendedores(objRaiz.getArray("vendedores"));
            this.listaEdificios = parseEdificios(objRaiz.getArray("edificios"));

            return true;
        } catch (Exception e) {
            System.out.println("[ERRO] Falha ao ler arquivo JSON. Dados em memoria iniciados vazios.");
            System.out.println("Detalhe: " + e.getMessage());
            this.listaVendedores = new ArrayList<>();
            this.listaEdificios = new ArrayList<>();
            return false;
        }
    }

    private String gerarJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"vendedores\":");
        sb.append(gerarJsonVendedores(this.listaVendedores));
        sb.append(",");
        sb.append("\"edificios\":");
        sb.append(gerarJsonEdificios(this.listaEdificios));
        sb.append("}");
        return sb.toString();
    }

    private String gerarJsonVendedores(List<Vendedor> vendedores) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vendedores.size(); i++) {
            Vendedor vendedor = vendedores.get(i);
            if (i > 0) {
                sb.append(",");
            }
            sb.append("{");
            sb.append("\"idVendedor\":").append(vendedor.getIdVendedor()).append(",");
            sb.append("\"nome\":\"").append(escape(vendedor.getNome())).append("\"");
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private String gerarJsonEdificios(List<Edificio> edificios) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < edificios.size(); i++) {
            Edificio edificio = edificios.get(i);
            if (i > 0) {
                sb.append(",");
            }
            sb.append("{");
            sb.append("\"id\":").append(edificio.getId()).append(",");
            sb.append("\"nome\":\"").append(escape(edificio.getNome())).append("\",");
            sb.append("\"endereco\":\"").append(escape(edificio.getEndereco())).append("\",");
            sb.append("\"andares\":").append(gerarJsonAndares(edificio.getAndares()));
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private String gerarJsonAndares(List<Andar> andares) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < andares.size(); i++) {
            Andar andar = andares.get(i);
            if (i > 0) {
                sb.append(",");
            }
            sb.append("{");
            sb.append("\"numero\":").append(andar.getNumero()).append(",");
            sb.append("\"quantidadeDeApartamentos\":").append(andar.getQuantidadeDeApartamentos()).append(",");
            sb.append("\"apartamentos\":").append(gerarJsonApartamentos(andar.getApartamentos()));
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private String gerarJsonApartamentos(List<Apartamento> apartamentos) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < apartamentos.size(); i++) {
            Apartamento apt = apartamentos.get(i);
            if (i > 0) {
                sb.append(",");
            }
            sb.append("{");
            sb.append("\"numero\":").append(apt.getNumero()).append(",");
            sb.append("\"andar\":").append(apt.getAndar()).append(",");
            sb.append("\"metragem\":").append(apt.getMetragem()).append(",");
            sb.append("\"quantidadeDeQuartos\":").append(apt.getQuantidadeDeQuartos()).append(",");
            sb.append("\"quantidadeDeBanheiros\":").append(apt.getQuantidadeDeBanheiros()).append(",");
            sb.append("\"valorDeVenda\":").append(apt.getValorDeVenda()).append(",");
            sb.append("\"valorSinal\":").append(apt.getValorSinal()).append(",");
            sb.append("\"status\":\"").append(apt.getStatus().name()).append("\"");
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private ArrayList<Vendedor> parseVendedores(JsonArray arr) {
        ArrayList<Vendedor> resultado = new ArrayList<>();
        if (arr == null) {
            return resultado;
        }

        for (JsonValue item : arr.values) {
            JsonObject obj = asObject(item);
            int id = obj.getInt("idVendedor", 0);
            String nome = obj.getString("nome", "");
            resultado.add(new Vendedor(id, nome));
        }
        return resultado;
    }

    private ArrayList<Edificio> parseEdificios(JsonArray arr) {
        ArrayList<Edificio> resultado = new ArrayList<>();
        if (arr == null) {
            return resultado;
        }

        for (JsonValue item : arr.values) {
            JsonObject objEdificio = asObject(item);
            int id = objEdificio.getInt("id", 0);
            String nome = objEdificio.getString("nome", "");
            String endereco = objEdificio.getString("endereco", "");

            Edificio edificio = new Edificio(id, nome, endereco);
            JsonArray andaresArr = objEdificio.getArray("andares");
            if (andaresArr != null) {
                for (JsonValue andarValue : andaresArr.values) {
                    JsonObject objAndar = asObject(andarValue);
                    int numeroAndar = objAndar.getInt("numero", 0);
                    int qtdApt = objAndar.getInt("quantidadeDeApartamentos", 0);
                    Andar andar = new Andar(numeroAndar, qtdApt);

                    JsonArray aptosArr = objAndar.getArray("apartamentos");
                    if (aptosArr != null) {
                        for (JsonValue aptValue : aptosArr.values) {
                            JsonObject objApt = asObject(aptValue);
                            Apartamento apt = new Apartamento(
                                    objApt.getInt("numero", 0),
                                    objApt.getInt("andar", numeroAndar),
                                    objApt.getDouble("metragem", 0.0),
                                    objApt.getInt("quantidadeDeQuartos", 0),
                                    objApt.getInt("quantidadeDeBanheiros", 0),
                                    objApt.getDouble("valorDeVenda", 0.0)
                            );
                            apt.setValorSinal(objApt.getDouble("valorSinal", 0.0));
                            String status = objApt.getString("status", StatusApartamento.DISPONIVEL.name());
                            try {
                                apt.setStatus(StatusApartamento.valueOf(status));
                            } catch (IllegalArgumentException ignored) {
                                apt.setStatus(StatusApartamento.DISPONIVEL);
                            }
                            andar.getApartamentos().add(apt);
                        }
                    }
                    edificio.getAndares().add(andar);
                }
            }

            resultado.add(edificio);
        }
        return resultado;
    }

    private JsonObject asObject(JsonValue value) {
        if (!(value instanceof JsonObject)) {
            throw new IllegalArgumentException("Item esperado como objeto JSON");
        }
        return (JsonObject) value;
    }

    private String escape(String texto) {
        if (texto == null) {
            return "";
        }
        return texto
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private interface JsonValue {}

    private static class JsonString implements JsonValue {
        private final String value;
        private JsonString(String value) {
            this.value = value;
        }
    }

    private static class JsonNumber implements JsonValue {
        private final double value;
        private JsonNumber(double value) {
            this.value = value;
        }
    }

    private static class JsonArray implements JsonValue {
        private final ArrayList<JsonValue> values = new ArrayList<>();
    }

    private static class JsonObject implements JsonValue {
        private final java.util.HashMap<String, JsonValue> values = new java.util.HashMap<>();

        private String getString(String key, String defaultValue) {
            JsonValue value = values.get(key);
            if (value instanceof JsonString) {
                return ((JsonString) value).value;
            }
            return defaultValue;
        }

        private int getInt(String key, int defaultValue) {
            JsonValue value = values.get(key);
            if (value instanceof JsonNumber) {
                return (int) ((JsonNumber) value).value;
            }
            return defaultValue;
        }

        private double getDouble(String key, double defaultValue) {
            JsonValue value = values.get(key);
            if (value instanceof JsonNumber) {
                return ((JsonNumber) value).value;
            }
            return defaultValue;
        }

        private JsonArray getArray(String key) {
            JsonValue value = values.get(key);
            if (value instanceof JsonArray) {
                return (JsonArray) value;
            }
            return null;
        }
    }

    private static class JsonParser {
        private final String text;
        private int pos;

        private JsonParser(String text) {
            this.text = text;
            this.pos = 0;
        }

        private JsonValue parse() {
            skipSpaces();
            JsonValue value = parseValue();
            skipSpaces();
            if (pos != text.length()) {
                throw new IllegalArgumentException("Conteudo extra apos JSON valido");
            }
            return value;
        }

        private JsonValue parseValue() {
            skipSpaces();
            if (pos >= text.length()) {
                throw new IllegalArgumentException("JSON inesperadamente finalizado");
            }

            char c = text.charAt(pos);
            if (c == '{') {
                return parseObject();
            }
            if (c == '[') {
                return parseArray();
            }
            if (c == '"') {
                return new JsonString(parseString());
            }
            if (c == '-' || Character.isDigit(c)) {
                return new JsonNumber(parseNumber());
            }

            throw new IllegalArgumentException("Valor JSON invalido na posicao " + pos);
        }

        private JsonObject parseObject() {
            expect('{');
            JsonObject obj = new JsonObject();
            skipSpaces();
            if (peek('}')) {
                expect('}');
                return obj;
            }

            while (true) {
                skipSpaces();
                String key = parseString();
                skipSpaces();
                expect(':');
                skipSpaces();
                JsonValue value = parseValue();
                obj.values.put(key, value);
                skipSpaces();
                if (peek('}')) {
                    expect('}');
                    break;
                }
                expect(',');
            }
            return obj;
        }

        private JsonArray parseArray() {
            expect('[');
            JsonArray arr = new JsonArray();
            skipSpaces();
            if (peek(']')) {
                expect(']');
                return arr;
            }

            while (true) {
                skipSpaces();
                arr.values.add(parseValue());
                skipSpaces();
                if (peek(']')) {
                    expect(']');
                    break;
                }
                expect(',');
            }
            return arr;
        }

        private String parseString() {
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (pos < text.length()) {
                char c = text.charAt(pos++);
                if (c == '"') {
                    return sb.toString();
                }
                if (c == '\\') {
                    if (pos >= text.length()) {
                        throw new IllegalArgumentException("Escape invalido em string JSON");
                    }
                    char e = text.charAt(pos++);
                    switch (e) {
                        case '"':
                            sb.append('"');
                            break;
                        case '\\':
                            sb.append('\\');
                            break;
                        case '/':
                            sb.append('/');
                            break;
                        case 'b':
                            sb.append('\b');
                            break;
                        case 'f':
                            sb.append('\f');
                            break;
                        case 'n':
                            sb.append('\n');
                            break;
                        case 'r':
                            sb.append('\r');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        default:
                            throw new IllegalArgumentException("Escape nao suportado: \\" + e);
                    }
                } else {
                    sb.append(c);
                }
            }
            throw new IllegalArgumentException("String JSON nao finalizada");
        }

        private double parseNumber() {
            int start = pos;
            if (peek('-')) {
                pos++;
            }

            while (pos < text.length() && Character.isDigit(text.charAt(pos))) {
                pos++;
            }

            if (pos < text.length() && text.charAt(pos) == '.') {
                pos++;
                while (pos < text.length() && Character.isDigit(text.charAt(pos))) {
                    pos++;
                }
            }

            String numero = text.substring(start, pos);
            try {
                return Double.parseDouble(numero);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Numero JSON invalido: " + numero);
            }
        }

        private void skipSpaces() {
            while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
                pos++;
            }
        }

        private boolean peek(char expected) {
            return pos < text.length() && text.charAt(pos) == expected;
        }

        private void expect(char expected) {
            if (pos >= text.length() || text.charAt(pos) != expected) {
                throw new IllegalArgumentException("Esperado '" + expected + "' na posicao " + pos);
            }
            pos++;
        }
    }
}
