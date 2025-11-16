// 설정
const BOARD_SIZE = 19;
const CELL_SIZE = 30;
const PADDING = 20;
const BOARD_WIDTH = CELL_SIZE * (BOARD_SIZE - 1) + PADDING * 2;

// 화점 위치 (19x19 기준)
const STAR_POINTS = [
    [3, 3], [3, 9], [3, 15],
    [9, 3], [9, 9], [9, 15],
    [15, 3], [15, 9], [15, 15]
];

// API URL
const API_BASE = '/api';

// 게임 상태
let gameState = null;
let bluespotPosition = null;
let lastMovePosition = null;

// DOM 요소
const board = document.getElementById('baduk-board');
const currentTurnEl = document.getElementById('current-turn');
const blackCapturesEl = document.getElementById('black-captures');
const whiteCapturesEl = document.getElementById('white-captures');
const moveCountEl = document.getElementById('move-count');
const moveListEl = document.getElementById('move-list');
const toastEl = document.getElementById('toast');
const scoreModal = document.getElementById('score-modal');
const scoreResultEl = document.getElementById('score-result');
const modalCloseBtn = document.getElementById('modal-close');

// 버튼
const newGameBtn = document.getElementById('new-game-btn');
const undoBtn = document.getElementById('undo-btn');
const recommendBtn = document.getElementById('recommend-btn');
const analysisBtn = document.getElementById('analysis-btn');
const scoreBtn = document.getElementById('score-btn');
const newGameFromModalBtn = document.getElementById('new-game-from-modal');

// 초기화
function init() {
    setupBoard();
    setupEventListeners();
    startNewGame();
}

// 바둑판 SVG 설정
function setupBoard() {
    board.setAttribute('width', BOARD_WIDTH);
    board.setAttribute('height', BOARD_WIDTH);

    // 격자선 그리기
    for (let i = 0; i < BOARD_SIZE; i++) {
        // 가로선
        const hLine = createSVGElement('line', {
            x1: PADDING,
            y1: PADDING + i * CELL_SIZE,
            x2: BOARD_WIDTH - PADDING,
            y2: PADDING + i * CELL_SIZE,
            stroke: '#000',
            'stroke-width': '1'
        });
        board.appendChild(hLine);

        // 세로선
        const vLine = createSVGElement('line', {
            x1: PADDING + i * CELL_SIZE,
            y1: PADDING,
            x2: PADDING + i * CELL_SIZE,
            y2: BOARD_WIDTH - PADDING,
            stroke: '#000',
            'stroke-width': '1'
        });
        board.appendChild(vLine);
    }

    // 화점 표시
    STAR_POINTS.forEach(([x, y]) => {
        const star = createSVGElement('circle', {
            cx: PADDING + x * CELL_SIZE,
            cy: PADDING + y * CELL_SIZE,
            r: '4',
            fill: '#000'
        });
        board.appendChild(star);
    });
}

// SVG 요소 생성 헬퍼
function createSVGElement(tag, attrs) {
    const el = document.createElementNS('http://www.w3.org/2000/svg', tag);
    Object.entries(attrs).forEach(([key, value]) => {
        el.setAttribute(key, value);
    });
    return el;
}

// 이벤트 리스너 설정
function setupEventListeners() {
    board.addEventListener('click', handleBoardClick);
    board.addEventListener('mousemove', handleBoardHover);
    board.addEventListener('mouseleave', clearHoverPreview);
    newGameBtn.addEventListener('click', startNewGame);
    undoBtn.addEventListener('click', handleUndo);
    recommendBtn.addEventListener('click', handleRecommend);
    analysisBtn.addEventListener('click', handleAnalysis);
    scoreBtn.addEventListener('click', handleScore);
    modalCloseBtn.addEventListener('click', closeModal);
    newGameFromModalBtn.addEventListener('click', () => {
        closeModal();
        startNewGame();
    });

    // 모달 바깥 클릭시 닫기
    scoreModal.addEventListener('click', (e) => {
        if (e.target === scoreModal) {
            closeModal();
        }
    });
}

// 바둑판 클릭 처리
async function handleBoardClick(e) {
    const rect = board.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;

    // 가장 가까운 교점 찾기
    const gridX = Math.round((x - PADDING) / CELL_SIZE) + 1; // 백엔드는 1-based
    const gridY = Math.round((y - PADDING) / CELL_SIZE) + 1;

    // 범위 체크
    if (gridX < 1 || gridX > BOARD_SIZE || gridY < 1 || gridY > BOARD_SIZE) {
        return;
    }

    // 착수 시도
    await makeMove(gridX, gridY);
}

// 바둑판 호버 처리 (미리보기)
function handleBoardHover(e) {
    if (!gameState) return;

    const rect = board.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;

    // 가장 가까운 교점 찾기
    const gridX = Math.round((x - PADDING) / CELL_SIZE) + 1;
    const gridY = Math.round((y - PADDING) / CELL_SIZE) + 1;

    // 범위 체크
    if (gridX < 1 || gridX > BOARD_SIZE || gridY < 1 || gridY > BOARD_SIZE) {
        clearHoverPreview();
        return;
    }

    // 해당 위치가 비어있는지 확인
    const isEmpty = gameState.board[gridX - 1][gridY - 1] === 'EMPTY';

    if (isEmpty) {
        showHoverPreview(gridX, gridY);
    } else {
        clearHoverPreview();
    }
}

// 호버 미리보기 표시
function showHoverPreview(gridX, gridY) {
    // 기존 호버 제거
    clearHoverPreview();

    // 현재 차례 색상
    const color = gameState.currentTurn === 'BLACK' ? '#000' : '#fff';
    const strokeColor = gameState.currentTurn === 'BLACK' ? '#000' : '#333';

    const preview = createSVGElement('circle', {
        cx: PADDING + (gridX - 1) * CELL_SIZE,
        cy: PADDING + (gridY - 1) * CELL_SIZE,
        r: '13',
        fill: color,
        stroke: strokeColor,
        'stroke-width': '1',
        class: 'hover-preview'
    });

    board.appendChild(preview);
}

// 호버 미리보기 제거
function clearHoverPreview() {
    const previews = board.querySelectorAll('.hover-preview');
    previews.forEach(preview => preview.remove());
}

// API 호출 헬퍼
async function apiCall(url, options = {}) {
    try {
        const response = await fetch(API_BASE + url, {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.error || '서버 오류가 발생했습니다.');
        }

        return data;
    } catch (error) {
        showToast(error.message, 'error');
        throw error;
    }
}

// 새 게임 시작
async function startNewGame() {
    try {
        const data = await apiCall('/game/start', { method: 'POST' });
        gameState = data;
        bluespotPosition = null;
        lastMovePosition = null;
        updateUI();
        showToast('새 게임이 시작되었습니다!', 'success');
    } catch (error) {
        console.error('게임 시작 실패:', error);
    }
}

// 착수
async function makeMove(x, y) {
    try {
        const data = await apiCall('/game/move', {
            method: 'POST',
            body: JSON.stringify({ x, y })
        });

        gameState = data.gameState;
        bluespotPosition = null; // 착수 추천 초기화
        lastMovePosition = { x, y }; // 마지막 착수 저장
        updateUI();
        showToast(data.message, 'success');
    } catch (error) {
        // 에러는 apiCall에서 이미 표시됨
    }
}

// 무르기
async function handleUndo() {
    try {
        const data = await apiCall('/game/move', { method: 'DELETE' });
        gameState = data.gameState;
        bluespotPosition = null;

        // 마지막 착수 업데이트 (착수 기록에서 가져오기)
        const moves = await apiCall('/game/moves');
        if (moves.length > 0) {
            const lastMove = moves[moves.length - 1];
            lastMovePosition = { x: lastMove.x, y: lastMove.y };
        } else {
            lastMovePosition = null;
        }

        updateUI();
        showToast(data.message, 'success');
    } catch (error) {
        // 에러는 apiCall에서 이미 표시됨
    }
}

// 착수 추천
async function handleRecommend() {
    try {
        const data = await apiCall('/katago/bluespots');
        bluespotPosition = { x: data.x, y: data.y };
        updateUI();
        showToast(`추천 위치: (${data.x}, ${data.y})`, 'info');
    } catch (error) {
        console.error('착수 추천 실패:', error);
    }
}

// 형세 판단 (게임 계속)
async function handleAnalysis() {
    try {
        const data = await apiCall('/katago/score');
        const result = formatScoreResult(data.result, '우세');
        showToast(result, 'info');
    } catch (error) {
        console.error('형세 판단 실패:', error);
    }
}

// 계가 (게임 종료 + 모달)
async function handleScore() {
    try {
        const data = await apiCall('/katago/score');
        showScoreModal(data.result);
    } catch (error) {
        console.error('계가 실패:', error);
    }
}

// UI 업데이트
async function updateUI() {
    if (!gameState) return;

    // 게임 정보 업데이트
    currentTurnEl.textContent = gameState.currentTurn === 'BLACK' ? '흑' : '백';
    blackCapturesEl.textContent = gameState.blackCaptures;
    whiteCapturesEl.textContent = gameState.whiteCaptures;
    moveCountEl.textContent = gameState.moveCount;

    // 바둑판 업데이트
    renderBoard();

    // 착수 기록 업데이트
    await updateMoveHistory();
}

// 바둑판 렌더링
function renderBoard() {
    // 기존 돌 제거
    const stones = board.querySelectorAll('.stone, .bluespot, .last-move-marker');
    stones.forEach(stone => stone.remove());

    // 돌 그리기
    if (gameState && gameState.board) {
        for (let x = 0; x < BOARD_SIZE; x++) {
            for (let y = 0; y < BOARD_SIZE; y++) {
                const stone = gameState.board[x][y];
                if (stone !== 'EMPTY') {
                    const color = stone === 'BLACK' ? '#000' : '#fff';
                    const strokeColor = stone === 'BLACK' ? '#000' : '#333';

                    const circle = createSVGElement('circle', {
                        cx: PADDING + x * CELL_SIZE,
                        cy: PADDING + y * CELL_SIZE,
                        r: '13',
                        fill: color,
                        stroke: strokeColor,
                        'stroke-width': '1',
                        class: 'stone'
                    });
                    board.appendChild(circle);
                }
            }
        }
    }

    // 마지막 착수 표시
    if (lastMovePosition && gameState) {
        const stoneColor = gameState.board[lastMovePosition.x - 1][lastMovePosition.y - 1];
        const markerClass = stoneColor === 'BLACK' ? 'on-black' : 'on-white';

        const marker = createSVGElement('circle', {
            cx: PADDING + (lastMovePosition.x - 1) * CELL_SIZE,
            cy: PADDING + (lastMovePosition.y - 1) * CELL_SIZE,
            r: '5',
            class: `last-move-marker ${markerClass}`
        });
        board.appendChild(marker);
    }

    // 블루스팟 표시
    if (bluespotPosition) {
        const bluespot = createSVGElement('circle', {
            cx: PADDING + (bluespotPosition.x - 1) * CELL_SIZE,
            cy: PADDING + (bluespotPosition.y - 1) * CELL_SIZE,
            r: '8',
            fill: '#4682B4',
            opacity: '0.7',
            class: 'bluespot'
        });
        board.appendChild(bluespot);
    }
}

// 착수 기록 업데이트
async function updateMoveHistory() {
    try {
        const moves = await apiCall('/game/moves');

        moveListEl.innerHTML = '';

        if (moves.length === 0) {
            moveListEl.innerHTML = '<div style="text-align: center; color: #999;">아직 착수가 없습니다</div>';
            return;
        }

        moves.forEach(move => {
            const moveItem = document.createElement('div');
            moveItem.className = `move-item ${move.color.toLowerCase()}`;
            moveItem.textContent = `${move.moveNumber}. ${move.color === 'BLACK' ? '흑' : '백'} (${move.x}, ${move.y})`;
            moveListEl.appendChild(moveItem);
        });

        // 스크롤을 맨 아래로
        moveListEl.scrollTop = moveListEl.scrollHeight;
    } catch (error) {
        console.error('착수 기록 조회 실패:', error);
    }
}

// 토스트 메시지 표시
function showToast(message, type = 'info') {
    toastEl.textContent = message;
    toastEl.className = `toast ${type} show`;

    setTimeout(() => {
        toastEl.classList.remove('show');
    }, 3000);
}

// 점수 결과 포맷팅 (공통 함수)
function formatScoreResult(scoreResult, suffix) {
    // 결과 파싱: "W+6.5" 또는 "B+12.5"
    const isWhiteWin = scoreResult.startsWith('W');
    const score = scoreResult.substring(2); // "+6.5" 부분
    const winner = isWhiteWin ? '백' : '흑';

    return `[${winner} ${score}집 ${suffix}]`;
}

// 계가 결과 모달 표시
function showScoreModal(scoreResult) {
    const resultText = formatScoreResult(scoreResult, '승리');
    const isWhiteWin = scoreResult.startsWith('W');

    scoreResultEl.textContent = resultText;
    scoreResultEl.className = `score-result ${isWhiteWin ? 'white-win' : 'black-win'}`;

    scoreModal.classList.add('show');
}

// 모달 닫기
function closeModal() {
    scoreModal.classList.remove('show');
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', init);