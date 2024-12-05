import java.sql.*;

public class ContaDAO {
    private Connection conectar() {
        Connection conexao = null;
        try {
            /*
             * O método getConnection da classe DriverManager é utilizado para estabelecer
             * uma conexão com o banco de dados.
             * O primeiro argumento é a URL de conexão com o banco de dados, que no caso é
             * jdbc:sqlite:database.db.
             */
            conexao = DriverManager.getConnection("jdbc:sqlite:database.db");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
        return conexao;
    }

    public void criarTabela() {
        // A tabela contacorrente é criada com duas colunas: numero e saldo.
        String sql_contacorrente = "CREATE TABLE IF NOT EXISTS contacorrente (" +
                     "numero INTEGER PRIMARY KEY, " +
                     "saldo REAL)";
        String sql_historico = "CREATE TABLE IF NOT EXISTS historico (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "tipo INTEGER, " +
                     "origem INTEGER, " +
                     "destino INTEGER, " +
                     "valor REAL, " +
                     "datahora TIMESTAMP);";
        // A conexão com o banco de dados é estabelecida e a tabela é criada.
        try (Connection conn = this.conectar();
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql_contacorrente);
            stmt.execute(sql_historico);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void inserirHistorico(int tipo, int origem, int destino, double valor) {
        String sql = "INSERT INTO historico(tipo, origem, destino, valor, datahora) VALUES(?, ?, ?, ?, ?)";
        // A conexão com o banco de dados é estabelecida e o histórico é inserido na tabela.
        try (Connection conn = this.conectar();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Os valores são utilizados para preencher os parâmetros da query.
            pstmt.setInt(1, tipo);
            pstmt.setInt(2, origem);
            pstmt.setInt(3, destino);
            pstmt.setDouble(4, valor);
            pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void inserirConta(Conta conta) {
        String sql = "INSERT INTO contacorrente(numero, saldo) VALUES(?, ?)";
        // A conexão com o banco de dados é estabelecida e a conta é inserida na tabela.
        try (Connection conn = this.conectar();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Os valores do objeto conta são utilizados para preencher os parâmetros da
            // query.
            pstmt.setInt(1, conta.getNumero());
            pstmt.setDouble(2, conta.getSaldo());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /*
    * O método é utilizado para buscar uma conta no banco de dados a partir do número
    * da conta.
    */
    public Conta buscarConta(int numeroConta) {
        String sql = "SELECT numero, saldo FROM contacorrente WHERE numero = ?";
        Conta conta = null;
        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, numeroConta);
            // O método executeQuery é utilizado para executar uma query de consulta.
            ResultSet rs = pstmt.executeQuery();
            // Se o resultado da consulta não estiver vazio, é criado um objeto conta com
            // os valores retornados.
            if (rs.next()) {
                conta = new Conta(rs.getInt("numero"), rs.getDouble("saldo"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conta;
    }

    /*
    * O método é utilizado para atualizar o saldo de uma conta no banco de dados.
    */
    public void atualizarSaldo(Conta conta) {
        String sql = "UPDATE contacorrente SET saldo = ? WHERE numero = ?";
        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Os valores do objeto conta são utilizados para preencher os parâmetros da
            // query.
            pstmt.setDouble(1, conta.getSaldo());
            pstmt.setInt(2, conta.getNumero());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}