
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

const host = "backend-service:5000";

async function calculate(value){
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
                await sendCalculation(historique);
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
}

async function sendCalculation(calcul) {
    try {
        const response = await fetch(`http://${host_test}/v1/calculate`, {
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
                console.error("[sendCalculation] Réquete POST | Erreur : " + data.error);
                updateDisplay("Error: " + data.error);                
            }
        } else {
            updateDisplay("Updating"+ error.message);                
            setTimeout(() => {
                getResult(operationId);
            }, 3000);            
        }
    } catch (error) {
        console.error("[sendCalculation] Réquete POST | Erreur réseau : " + error.message);
        updateDisplay("Network error: " + error.message);
    }
}

async function getResult(operationId) {
    try {
        const response = await fetch(`http://${host_test}/v1/result/${operationId}`);
        const data = await response.json();

        if (response.ok) {
            updateDisplay(data.result);
            previousInput=data.result
        } else {
            console.error("[getResult] Erreur : " + data.error);
            updateDisplay("Error: " + data.error);
        }
    } catch (error) {
        console.error("[getResult] Erreur réseau : " + error.message);
        updateDisplay("Network error: " + error.message);
    }
}

buttons.forEach(button => {
    button.addEventListener('click', async () => {
        const value = button.dataset.value;
        await calculate(value);
    });
});

document.addEventListener("keydown", async function (e) {
    switch (e.key) {
        case "0":
        case "1":
        case "2":
        case "3":
        case "4":
        case "5":
        case "6":
        case "7":
        case "8":
        case "9":                
        case "+":
        case "-":
        case "*":
        case "/":
        case ".":
            await calculate(e.key);
            break;        
        case "Enter":      
            calculate("=");
            break;
        case "Backspace":   
            calculate("C");
            break;    
        case "Escape":
            calculate("AC");
            break;
        case "%":
            break;
        default:
            return; // Do nothing for the rest
    }
});