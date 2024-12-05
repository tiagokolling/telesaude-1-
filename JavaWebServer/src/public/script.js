document.getElementById('buscarConta').addEventListener('click', () => {
    const numeroConta = document.getElementById('numeroConta').value;
    fetch(`/conta?numero=${numeroConta}`)
        .then(response => {
            if (!response.ok) {
                return response.json().then(error => {
                    throw new Error(error.error);
                });
            }
            return response.json();
        })
        .then(data => {
            if (data.erro) {
                document.getElementById('resultado').innerText = data.erro;
            } else {
                document.getElementById('resultado').innerHTML = `
                    <p><strong>Conta:</strong> ${data.numero}</p>
                    <p><strong>Saldo:</strong> R$ ${data.saldo}</p>
                `;
            }
        })
        .catch(error => {
            document.getElementById('resultado').innerText = "Erro na conexão com o servidor: " + error.message;
        });
});