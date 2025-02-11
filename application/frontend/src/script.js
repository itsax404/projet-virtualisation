
const display = document.getElementById('display');
const historiqueDisplay=document.getElementById('historique')
const buttons = document.querySelectorAll('button');


let currentInput = ''; 
let previousInput = ''; 
let operator = ''; 
let historique='';

function updateDisplay(value) {
    display.textContent = value;
}
function updateHistorique(value){
    historiqueDisplay.textContent = value;
}

const host="10.2.7.105:5000";

async function sendCalculation(calcul) {
    try {
        const response = await fetch(`http://${host}/api/v1/calculate`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ calcul: calcul })
        });

        const data = await response.json();
        if (response.ok) {
            const operationId = data.operation_id;
            try{
                await getResult(operationId);
            }
            catch(error){
                updateDisplay("Error: " + data.error);                
            }
        } else {
            updateDisplay("Updating"+ error.message);                
            setTimeout(() => {
                getResult(operationId);
            }, 3000);            
        }
    } catch (error) {
        updateDisplay("Network error: " + error.message);
    }
}

async function getResult(operationId) {
    try {
        const response = await fetch(`http://${host}/api/v1/result/${operationId}`);
        const data = await response.json();

        if (response.ok) {
            updateDisplay(data.result);
            previousInput=data.result
        } else {
            updateDisplay("Error: " + data.error);
        }
    } catch (error) {
        updateDisplay("Network error: " + error.message);
    }
}

buttons.forEach(button => {
    button.addEventListener('click', () => {
        const value = button.dataset.value;

        if (value === 'AC') {
            currentInput = '';
            previousInput = '';
            operator = '';
            updateDisplay('0');
        } else if (value === 'C') {
            currentInput = currentInput.slice(0, -1);
            updateDisplay(currentInput || '0');
        } else if (value === '=') {
            if (currentInput) {
                if (previousInput){
                    historique=previousInput+operator+currentInput;
                    sendCalculation(historique);
                    updateHistorique(historique);
                    previousInput = '';
                    currentInput = '';
                    operator = '';
                }
            }
        } else if (['+', '-', '*', '/'].includes(value)) {
            if (currentInput) {
                if (previousInput) {
                    previousInput = previousInput+value+currentInput;
                } else {
                    previousInput = currentInput;
                }
                currentInput = '';
            }
            operator = value;
            updateHistorique(previousInput);
        } else {
            if (value === '.' && currentInput.includes('.')) return;
            currentInput += value;
            updateDisplay(currentInput);
        }
    });
});
