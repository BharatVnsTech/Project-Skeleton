(function () {
    'use strict';

    const BASE_URL_DEFAULT = 'http://localhost:8080';
    const HISTORY_KEY = 'api-console-history';
    const MAX_HISTORY = 20;

    const SERVICE_MAP = {
        '/api/v1/users': { name: 'USER-SERVICE', port: 8081 },
        '/api/v1/parking': { name: 'PARKING-SERVICE', port: 8082 },
        '/api/v1/payments': { name: 'PAYMENT-SERVICE', port: 8083 }
    };

    const HEALTH_ENDPOINTS = {
        gateway: { url: 'http://localhost:8080/actuator/health', name: 'API GATEWAY' },
        discovery: { url: 'http://localhost:8761/actuator/health', name: 'SERVICE DISCOVERY' },
        user: { url: 'http://localhost:8081/actuator/health', name: 'USER SERVICE' },
        parking: { url: 'http://localhost:8082/actuator/health', name: 'PARKING SERVICE' },
        payment: { url: 'http://localhost:8083/actuator/health', name: 'PAYMENT SERVICE' }
    };

    const elements = {
        httpMethod: document.getElementById('httpMethod'),
        baseUrl: document.getElementById('baseUrl'),
        endpoint: document.getElementById('endpoint'),
        requestBody: document.getElementById('requestBody'),
        sendRequest: document.getElementById('sendRequest'),
        correlationIdInput: document.getElementById('correlationIdInput'),
        flowVisualization: document.getElementById('flowVisualization'),
        requestInfoSection: document.getElementById('requestInfoSection'),
        requestInfo: document.getElementById('requestInfo'),
        responseSection: document.getElementById('responseSection'),
        responseInfo: document.getElementById('responseInfo'),
        responseBody: document.getElementById('responseBody'),
        copyResponse: document.getElementById('copyResponse'),
        historyBody: document.getElementById('historyBody'),
        refreshStatus: document.getElementById('refreshStatus'),
        addHeader: document.getElementById('addHeader')
    };

    function generateCorrelationId() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    }

    function detectService(endpoint) {
        for (const [prefix, info] of Object.entries(SERVICE_MAP)) {
            if (endpoint.startsWith(prefix)) return info;
        }
        return { name: 'UNKNOWN', port: 'N/A' };
    }

    function getStatusClass(statusCode) {
        if (statusCode >= 200 && statusCode < 300) return 's2xx';
        if (statusCode >= 400 && statusCode < 500) return 's4xx';
        return 's5xx';
    }

    function getStatusText(code) {
        const map = {
            200: 'OK', 201: 'Created', 204: 'No Content',
            400: 'Bad Request', 404: 'Not Found', 500: 'Internal Server Error',
            502: 'Bad Gateway', 503: 'Service Unavailable'
        };
        return map[code] || '';
    }

    function formatJSON(str) {
        try {
            return JSON.stringify(JSON.parse(str), null, 2);
        } catch (e) {
            return str;
        }
    }

    function getTimeString() {
        return new Date().toLocaleTimeString('en-US', { hour12: false });
    }

    function getHeaders() {
        const headers = {};
        const rows = document.querySelectorAll('.header-row');
        rows.forEach(function (row) {
            const key = row.querySelector('.header-key').value.trim();
            const value = row.querySelector('.header-value').value.trim();
            if (key) headers[key] = value || '';
        });
        return headers;
    }

    // Flow Visualization
    function createFlowNode(id, title, status, detail) {
        const iconMap = {
            waiting: '\u25CB',
            processing: '\u27F3',
            completed: '\u2713',
            failed: '\u2715'
        };
        return '<div class="flow-node ' + status + '" id="flow-' + id + '">' +
            '<div class="flow-node-status">' + iconMap[status] + '</div>' +
            '<div class="flow-node-title">' + title + '</div>' +
            (detail ? '<div class="flow-node-detail">' + detail + '</div>' : '') +
            '<div class="flow-node-badge ' + status + '">' + status.toUpperCase() + '</div>' +
            '</div>';
    }

    function createFlowArrow(active) {
        return '<div class="flow-arrow ' + (active ? 'active' : '') + '">\u2193</div>';
    }

    function initFlow() {
        elements.flowVisualization.innerHTML =
            createFlowNode('client', 'CLIENT', 'waiting', 'Preparing request...') +
            createFlowArrow(false) +
            createFlowNode('gateway', 'API GATEWAY', 'waiting', 'Port: 8080') +
            createFlowArrow(false) +
            createFlowNode('discovery', 'SERVICE DISCOVERY', 'waiting', 'Eureka: 8761') +
            createFlowArrow(false) +
            createFlowNode('service', 'TARGET SERVICE', 'waiting', 'Awaiting routing...') +
            createFlowArrow(false) +
            createFlowNode('response', 'RESPONSE', 'waiting', 'Awaiting response...');
    }

    function updateFlowNode(id, status, detail) {
        const node = document.getElementById('flow-' + id);
        if (!node) return;
        node.className = 'flow-node ' + status;
        const iconMap = {
            waiting: '\u25CB', processing: '\u27F3', completed: '\u2713', failed: '\u2715'
        };
        node.querySelector('.flow-node-status').textContent = iconMap[status];
        node.querySelector('.flow-node-badge').className = 'flow-node-badge ' + status;
        node.querySelector('.flow-node-badge').textContent = status.toUpperCase();
        if (detail) {
            var detailEl = node.querySelector('.flow-node-detail');
            if (detailEl) detailEl.textContent = detail;
        }
    }

    function setArrowActive(index) {
        var arrows = elements.flowVisualization.querySelectorAll('.flow-arrow');
        arrows.forEach(function (a, i) {
            a.className = 'flow-arrow ' + (i <= index ? 'active' : '');
        });
    }

    // Send Request
    async function sendRequest() {
        var method = elements.httpMethod.value;
        var baseUrl = elements.baseUrl.value.replace(/\/+$/, '');
        var endpoint = elements.endpoint.value;
        var headers = getHeaders();
        var service = detectService(endpoint);
        var corrId = headers['X-Correlation-ID'] || generateCorrelationId();
        headers['X-Correlation-ID'] = corrId;

        elements.sendRequest.disabled = true;
        elements.sendRequest.textContent = 'SENDING...';
        initFlow();

        var startTime = performance.now();
        var targetUrl = baseUrl + endpoint;

        // Client processing
        updateFlowNode('client', 'processing', method + ' ' + endpoint);
        await delay(100);
        updateFlowNode('client', 'completed', method + ' ' + endpoint);
        setArrowActive(0);

        // Gateway processing
        updateFlowNode('gateway', 'processing', 'Port: 8080 -> Route: ' + service.name);
        await delay(150);
        updateFlowNode('gateway', 'completed', 'Route: ' + service.name);
        setArrowActive(1);

        // Discovery processing
        updateFlowNode('discovery', 'processing', 'Looking up ' + service.name);
        await delay(100);
        updateFlowNode('discovery', 'completed', 'Found ' + service.name + ' at localhost:' + service.port);
        setArrowActive(2);

        // Service processing
        updateFlowNode('service', 'processing', service.name + ' | Port: ' + service.port);
        setArrowActive(3);

        // Request info
        showRequestInfo(method, endpoint, baseUrl, service, corrId, startTime);

        try {
            var fetchOptions = {
                method: method,
                headers: headers,
                mode: 'cors'
            };

            if (method !== 'GET' && method !== 'DELETE' && elements.requestBody.value.trim()) {
                fetchOptions.body = elements.requestBody.value;
            }

            var response = await fetch(targetUrl, fetchOptions);
            var endTime = performance.now();
            var duration = Math.round(endTime - startTime);
            var responseText = await response.text();

            // Service completed
            if (response.ok) {
                updateFlowNode('service', 'completed', service.name + ' | HTTP ' + response.status);
            } else {
                updateFlowNode('service', 'failed', service.name + ' | HTTP ' + response.status);
            }

            // Response
            var responseStatus = response.ok ? 'completed' : 'failed';
            var responseDetail = 'HTTP ' + response.status + ' ' + getStatusText(response.status) + ' | ' + duration + 'ms';
            updateFlowNode('response', responseStatus, responseDetail);
            setArrowActive(4);

            // Show response
            showResponse(response.status, duration, corrId, service.name, responseText, response.headers);

            // History
            addHistory(method, endpoint, response.status, service.name);

        } catch (error) {
            var endTime = performance.now();
            var duration = Math.round(endTime - startTime);

            updateFlowNode('service', 'failed', 'Connection unavailable');
            updateFlowNode('response', 'failed', 'Request failed | ' + duration + 'ms');
            setArrowActive(4);

            showErrorResponse(error, duration, corrId, service.name);
            addHistory(method, endpoint, 0, service.name);
        }

        elements.sendRequest.disabled = false;
        elements.sendRequest.textContent = 'SEND REQUEST';
    }

    function showRequestInfo(method, endpoint, baseUrl, service, corrId, startTime) {
        elements.requestInfoSection.style.display = 'block';
        elements.requestInfo.innerHTML =
            '<div class="request-info-grid">' +
            '<div><div class="request-info-label">Method</div><div class="request-info-value">' + method + '</div></div>' +
            '<div><div class="request-info-label">Endpoint</div><div class="request-info-value">' + endpoint + '</div></div>' +
            '<div><div class="request-info-label">Gateway</div><div class="request-info-value">localhost:8080</div></div>' +
            '<div><div class="request-info-label">Target Service</div><div class="request-info-value">' + service.name + '</div></div>' +
            '<div><div class="request-info-label">Target Instance</div><div class="request-info-value">localhost:' + service.port + '</div></div>' +
            '<div><div class="request-info-label">Correlation ID</div><div class="request-info-value">' + corrId + '</div></div>' +
            '<div><div class="request-info-label">Start Time</div><div class="request-info-value">' + new Date().toISOString() + '</div></div>' +
            '<div><div class="request-info-label">Status</div><div class="request-info-value">In Progress</div></div>' +
            '</div>';
    }

    function showResponse(status, duration, corrId, serviceName, body, headers) {
        elements.responseSection.style.display = 'block';
        var statusClass = status >= 200 && status < 300 ? 'success' : (status >= 400 && status < 500 ? 'warning' : 'error');

        var returnedCorrelationId = headers ? (headers.get('X-Correlation-ID') || corrId) : corrId;

        elements.responseInfo.innerHTML =
            '<div>' +
            '<div class="response-meta-label">HTTP Status</div>' +
            '<div class="response-meta-value ' + statusClass + '">' + status + ' ' + getStatusText(status) + '</div>' +
            '</div>' +
            '<div>' +
            '<div class="response-meta-label">Service</div>' +
            '<div class="response-meta-value">' + serviceName + '</div>' +
            '</div>' +
            '<div>' +
            '<div class="response-meta-label">Response Time</div>' +
            '<div class="response-meta-value">' + duration + ' ms</div>' +
            '</div>' +
            '<div>' +
            '<div class="response-meta-label">Correlation ID</div>' +
            '<div class="response-meta-value">' + returnedCorrelationId + '</div>' +
            '</div>';

        elements.responseBody.textContent = formatJSON(body);
    }

    function showErrorResponse(error, duration, corrId, serviceName) {
        elements.responseSection.style.display = 'block';
        elements.responseInfo.innerHTML =
            '<div>' +
            '<div class="response-meta-label">Status</div>' +
            '<div class="response-meta-value error">FAILED</div>' +
            '</div>' +
            '<div>' +
            '<div class="response-meta-label">Service</div>' +
            '<div class="response-meta-value">' + serviceName + '</div>' +
            '</div>' +
            '<div>' +
            '<div class="response-meta-label">Duration</div>' +
            '<div class="response-meta-value">' + duration + ' ms</div>' +
            '</div>' +
            '<div>' +
            '<div class="response-meta-label">Correlation ID</div>' +
            '<div class="response-meta-value">' + corrId + '</div>' +
            '</div>';
        elements.responseBody.textContent = 'Error: ' + error.message + '\n\nThe service may be down or unreachable.\nMake sure the target microservice is running.';
    }

    function addHistory(method, endpoint, status, service) {
        var entry = {
            time: getTimeString(),
            method: method,
            endpoint: endpoint,
            status: status,
            service: service
        };

        var history = JSON.parse(localStorage.getItem(HISTORY_KEY) || '[]');
        history.unshift(entry);
        if (history.length > MAX_HISTORY) history = history.slice(0, MAX_HISTORY);
        localStorage.setItem(HISTORY_KEY, JSON.stringify(history));
        renderHistory();
    }

    function renderHistory() {
        var history = JSON.parse(localStorage.getItem(HISTORY_KEY) || '[]');
        elements.historyBody.innerHTML = history.map(function (h) {
            var statusText = h.status === 0 ? 'ERR' : h.status;
            var statusClass = h.status === 0 ? 's5xx' : getStatusClass(h.status);
            return '<tr>' +
                '<td>' + h.time + '</td>' +
                '<td>' + h.method + '</td>' +
                '<td>' + h.endpoint + '</td>' +
                '<td><span class="status-badge ' + statusClass + '">' + statusText + '</span></td>' +
                '<td>' + h.service + '</td>' +
                '</tr>';
        }).join('');
    }

    // Health checks
    async function checkServiceHealth(key, url) {
        try {
            var response = await fetch(url, { method: 'GET', mode: 'cors', signal: AbortSignal.timeout(3000) });
            return response.ok;
        } catch (e) {
            return false;
        }
    }

    async function refreshSystemStatus() {
        var items = document.querySelectorAll('.status-item');
        items.forEach(function (item) { item.className = 'status-item'; });

        for (var key in HEALTH_ENDPOINTS) {
            var info = HEALTH_ENDPOINTS[key];
            var isUp = await checkServiceHealth(key, info.url);
            var el = document.querySelector('.status-item[data-service="' + key + '"]');
            if (el) {
                el.className = 'status-item ' + (isUp ? 'up' : 'down');
                el.innerHTML = (isUp ? '\u2713 ' : '\u2715 ') + info.name;
            }
        }
    }

    function delay(ms) {
        return new Promise(function (resolve) { setTimeout(resolve, ms); });
    }

    // Event Listeners
    elements.httpMethod.addEventListener('change', function () {
        var isGet = this.value === 'GET' || this.value === 'DELETE';
        elements.requestBody.disabled = isGet;
        if (isGet) elements.requestBody.value = '';
    });

    elements.sendRequest.addEventListener('click', sendRequest);

    elements.refreshStatus.addEventListener('click', refreshSystemStatus);

    elements.copyResponse.addEventListener('click', function () {
        var text = elements.responseBody.textContent;
        navigator.clipboard.writeText(text).then(function () {
            elements.copyResponse.textContent = 'COPIED!';
            setTimeout(function () { elements.copyResponse.textContent = 'COPY RESPONSE'; }, 2000);
        });
    });

    elements.addHeader.addEventListener('click', function () {
        var section = document.querySelector('.headers-section');
        var row = document.createElement('div');
        row.className = 'header-row';
        row.innerHTML = '<input type="text" placeholder="Header name" class="header-key">' +
            '<input type="text" placeholder="Value" class="header-value">';
        section.insertBefore(row, elements.addHeader);
    });

    // Quick API buttons
    document.querySelectorAll('.btn-quick').forEach(function (btn) {
        btn.addEventListener('click', function () {
            var method = this.getAttribute('data-method');
            var endpoint = this.getAttribute('data-endpoint');
            var port = this.getAttribute('data-port');

            elements.httpMethod.value = method;
            elements.endpoint.value = endpoint;

            if (port) {
                elements.baseUrl.value = 'http://localhost:' + port;
            } else {
                elements.baseUrl.value = BASE_URL_DEFAULT;
            }

            var isGet = method === 'GET' || method === 'DELETE';
            elements.requestBody.disabled = isGet;
        });
    });

    // Initialize
    initFlow();
    renderHistory();
    refreshSystemStatus();
})();
