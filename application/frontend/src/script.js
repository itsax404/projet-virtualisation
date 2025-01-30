
const display = document.getElementById('display');
const buttons = document.querySelectorAll('button');


let currentInput = ''; 
let previousInput = ''; 
let operator = ''; 

function updateDisplay(value) {
    display.textContent = value;
}

async function sendCalculation(calcul) {
    try {
        const response = await fetch('http://localhost:5000/api/v1/myCalculatrice/calculate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ calcul: calcul })
        });

        const data = await response.json();
        if (response.ok) {
            const operationId = data.operation_id;
            await getResult(operationId);
        } else {
            updateDisplay("Error: " + data.error);
        }
    } catch (error) {
        updateDisplay("Network error: " + error.message);
    }
}

async function getResult(operationId) {
    try {
        const response = await fetch(`http://localhost:5000/api/v1/myCalculatrice/result/${operationId}`);
        const data = await response.json();

        if (response.ok) {
            updateDisplay(data.result);
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
                sendCalculation(currentInput);
                previousInput = currentInput;
                currentInput = '';
                operator = '';
            }
        } else if (['+', '-', '*', '/'].includes(value)) {
            if (currentInput) {
                if (previousInput) {
                    previousInput = currentInput;
                } else {
                    previousInput = currentInput;
                }
                currentInput = '';
            }
            operator = value;
        } else {
            if (value === '.' && currentInput.includes('.')) return;
            currentInput += value;
            updateDisplay(currentInput);
        }
    });
});
