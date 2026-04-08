const landingPage = document.getElementById('landing-page');
const authPage = document.getElementById('auth-page');
const dashboardPage = document.getElementById('dashboard-page');
const roleAdminButton = document.getElementById('role-admin');
const roleUserButton = document.getElementById('role-user');
const authForm = document.getElementById('auth-form');
const authSubmit = document.getElementById('auth-submit');
const authMessage = document.getElementById('auth-message');
const usernameInput = document.getElementById('username');
const passwordInput = document.getElementById('password');
const passwordConfirmInput = document.getElementById('password-confirm');
const authHeading = document.getElementById('auth-heading');
const authSubtitle = document.getElementById('auth-subtitle');
const authBack = document.getElementById('auth-back');
const registerSection = document.getElementById('register-section');
const authModeText = document.getElementById('auth-mode-text');
const switchAuthMode = document.getElementById('switch-auth-mode');
const userRoleLabel = document.getElementById('user-role');
const welcomeTitle = document.getElementById('welcome-title');
const dashboardSubTitle = document.getElementById('dashboard-subtitle');
const studyButton = document.getElementById('study-button');
const quizButton = document.getElementById('quiz-button');
const difficultySelect = document.getElementById('difficulty-select');
const activityPanel = document.getElementById('activity-panel');
const activityTitle = document.getElementById('activity-title');
const activityContent = document.getElementById('activity-content');
const backToDashboard = document.getElementById('back-to-dashboard');
const logoutButton = document.getElementById('logout-button');
const adminPanel = document.getElementById('admin-panel');
const questionForm = document.getElementById('question-form');
const questionMessage = document.getElementById('question-message');
const questionTableWrapper = document.getElementById('question-table-wrapper');
const historyButton = document.getElementById('history-button');
const historyPanel = document.getElementById('history-panel');
const historyContent = document.getElementById('history-content');
const backToDashboardHistory = document.getElementById('back-to-dashboard-history');

let accessRole = null;
let authMode = 'login';
let currentUser = null;
let editingQuestionId = null;
let currentQuiz = { questions: [], answers: {}, index: 0 };

function showLanding() {
  landingPage.classList.remove('hidden');
  authPage.classList.add('hidden');
  dashboardPage.classList.add('hidden');
}

function showAuth() {
  landingPage.classList.add('hidden');
  authPage.classList.remove('hidden');
  dashboardPage.classList.add('hidden');
}

function showDashboard() {
  landingPage.classList.add('hidden');
  authPage.classList.add('hidden');
  dashboardPage.classList.remove('hidden');
  userRoleLabel.textContent = currentUser.role.toUpperCase();
  welcomeTitle.textContent = `Welcome, ${currentUser.username}`;
  dashboardSubTitle.textContent = currentUser.role === 'admin'
    ? 'Manage questions and maintain the quiz library.'
    : 'Choose a difficulty and start learning.';
  adminPanel.classList.toggle('hidden', currentUser.role !== 'admin');
  activityPanel.classList.add('hidden');
  historyPanel.classList.add('hidden');
  activityTitle.textContent = '';
  activityContent.innerHTML = '';
  if (currentUser.role === 'admin') {
    loadAdminQuestions();
  }
}

function setAccessRole(role) {
  accessRole = role;
  authMode = 'login';
  authHeading.textContent = role === 'admin' ? 'Admin Sign In' : 'User Sign In';
  authSubtitle.textContent = role === 'admin'
    ? 'Use your admin credentials to manage the quiz database.'
    : 'Login or register to access study and quiz mode.';
  document.getElementById('auth-badge').textContent = role === 'admin' ? 'Admin access' : 'User access';
  authSubmit.textContent = role === 'admin' ? 'Login as Admin' : 'Login';
  registerSection.classList.toggle('hidden', role === 'admin');
  authModeText.textContent = role === 'admin' ? '' : "Don't have an account?";
  switchAuthMode.textContent = role === 'admin' ? '' : 'Register';
  switchAuthMode.classList.toggle('hidden', role === 'admin');
  passwordConfirmInput.value = '';
  authMessage.textContent = '';
}

function toggleAuthMode() {
  if (authMode === 'login') {
    authMode = 'register';
    authSubmit.textContent = 'Register';
    authModeText.textContent = 'Already have an account?';
    switchAuthMode.textContent = 'Login';
    registerSection.classList.remove('hidden');
  } else {
    authMode = 'login';
    authSubmit.textContent = accessRole === 'admin' ? 'Login as Admin' : 'Login';
    authModeText.textContent = "Don't have an account?";
    switchAuthMode.textContent = 'Register';
    registerSection.classList.add('hidden');
  }
  authMessage.textContent = '';
}

roleAdminButton.addEventListener('click', () => {
  setAccessRole('admin');
  showAuth();
});

roleUserButton.addEventListener('click', () => {
  setAccessRole('user');
  showAuth();
});

authBack.addEventListener('click', () => {
  showLanding();
});

logoutButton.addEventListener('click', () => {
  currentUser = null;
  usernameInput.value = '';
  passwordInput.value = '';
  passwordConfirmInput.value = '';
  editingQuestionId = null;
  questionForm.reset();
  questionSubmit.textContent = 'Save Question';
  questionCancel.classList.add('hidden');
  showLanding();
});

function api(path, method = 'GET', body = null) {
  const options = { method, headers: { 'Content-Type': 'application/json' } };
  if (body) options.body = JSON.stringify(body);
  return fetch(path, options).then((response) => response.json());
}

authForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  const username = usernameInput.value.trim();
  const password = passwordInput.value.trim();
  const confirmPassword = passwordConfirmInput.value.trim();

  if (!username || !password) {
    authMessage.textContent = 'Please complete both fields.';
    return;
  }

  if (accessRole === 'admin') {
    const result = await api('/api/login', 'POST', { username, password });
    if (!result.success || result.user.role !== 'admin') {
      authMessage.textContent = 'Invalid admin credentials.';
      return;
    }
    currentUser = result.user;
    showDashboard();
    return;
  }

  if (authMode === 'register') {
    if (password !== confirmPassword) {
      authMessage.textContent = 'Passwords do not match.';
      return;
    }
    const result = await api('/api/register', 'POST', { username, password });
    authMessage.textContent = result.message || 'Registration failed.';
    if (result.success) {
      authMode = 'login';
      authSubmit.textContent = 'Login';
      authModeText.textContent = "Don't have an account?";
      switchAuthMode.textContent = 'Register';
      registerSection.classList.add('hidden');
    }
    return;
  }

  const result = await api('/api/login', 'POST', { username, password });
  if (!result.success) {
    authMessage.textContent = result.message || 'Invalid credentials.';
    return;
  }
  if (result.user.role !== 'user') {
    authMessage.textContent = 'Please login with a user account.';
    return;
  }
  currentUser = result.user;
  showDashboard();
});

switchAuthMode.addEventListener('click', () => toggleAuthMode());

studyButton.addEventListener('click', () => startActivity('study'));
quizButton.addEventListener('click', () => startActivity('quiz'));
historyButton.addEventListener('click', () => showHistory());
backToDashboard.addEventListener('click', () => showDashboard());
backToDashboardHistory.addEventListener('click', () => showDashboard());

async function startActivity(mode) {
  const difficulty = difficultySelect.value;
  activityPanel.classList.remove('hidden');
  activityTitle.textContent = mode === 'study' ? `Study Mode (${difficulty})` : `Quiz Mode (${difficulty})`;
  activityContent.innerHTML = '<p class="message">Loading questions...</p>';

  const result = await api(`/api/questions?difficulty=${difficulty}`);
  if (!result.success) {
    activityContent.innerHTML = `<p class="message">${result.message || 'Unable to load questions.'}</p>`;
    return;
  }

  if (mode === 'study') {
    renderStudy(result.questions);
  } else {
    startQuiz(result.questions);
  }
}

function renderStudy(questions) {
  if (!questions.length) {
    activityContent.innerHTML = '<p class="message">No questions found for this difficulty.</p>';
    return;
  }

  let index = 0;
  let showAnswer = false;
  const selectedAnswers = {};

  function render() {
    const question = questions[index];
    const selected = selectedAnswers[question.id];

    const optionsHtml = [1, 2, 3, 4]
      .map((option) => {
        const isSelected = selected === option;
        const isCorrect = question.correctOption === option;
        const classes = ['option-button'];
        if (isSelected) classes.push('selected');
        if (showAnswer && isCorrect) classes.push('correct');
        if (showAnswer && isSelected && !isCorrect) classes.push('incorrect');
        return `
          <button class="${classes.join(' ')}" data-answer="${option}" type="button">
            ${question['option' + option]}
          </button>`;
      })
      .join('');

    activityContent.innerHTML = `
      <div class="question-card">
        <p class="eyebrow">Study question ${index + 1} of ${questions.length}</p>
        <h4>${question.question}</h4>
        <div class="option-list">${optionsHtml}</div>
        <div class="study-feedback">
          ${selected ? `<p>Your answer: <strong>${question['option' + selected]}</strong></p>` : '<p>Please select an option to check your answer.</p>'}
          ${showAnswer ? `<p class="message">Correct answer: <strong>${question['option' + question.correctOption]}</strong></p>` : ''}
        </div>
        <div class="option-row" style="margin-top:22px;">
          <button class="secondary-button" id="prev-study" ${index === 0 ? 'disabled' : ''}>Previous</button>
          <button class="secondary-button" id="toggle-answer">${showAnswer ? 'Hide Answer' : 'Show Answer'}</button>
          <button class="secondary-button" id="next-study" ${index === questions.length - 1 ? 'disabled' : ''}>Next</button>
        </div>
      </div>`;

    activityContent.querySelectorAll('.option-button').forEach((button) => {
      button.addEventListener('click', () => {
        selectedAnswers[question.id] = Number(button.dataset.answer);
        render();
      });
    });

    document.getElementById('prev-study').addEventListener('click', () => {
      if (index > 0) {
        index -= 1;
        showAnswer = false;
        render();
      }
    });

    document.getElementById('next-study').addEventListener('click', () => {
      if (index < questions.length - 1) {
        index += 1;
        showAnswer = false;
        render();
      }
    });

    document.getElementById('toggle-answer').addEventListener('click', () => {
      showAnswer = !showAnswer;
      render();
    });
  }

  render();
}

function startQuiz(questions) {
  currentQuiz.questions = questions;
  currentQuiz.answers = {};
  currentQuiz.index = 0;
  renderQuizQuestion();
}

function renderQuizQuestion() {
  const question = currentQuiz.questions[currentQuiz.index];
  if (!question) return;

  const answer = currentQuiz.answers[question.id];
  const optionsHtml = [1, 2, 3, 4]
    .map((option) => `
      <button class="option-button ${answer === option ? 'selected' : ''}" data-answer="${option}" type="button">
        ${question['option' + option]}
      </button>`)
    .join('');

  activityContent.innerHTML = `
    <div class="question-card">
      <p class="eyebrow">Quiz question ${currentQuiz.index + 1} of ${currentQuiz.questions.length}</p>
      <h4>${question.question}</h4>
      <div class="option-list">${optionsHtml}</div>
      <div class="option-row" style="margin-top:18px;">
        <button class="secondary-button" id="prev-question" ${currentQuiz.index === 0 ? 'disabled' : ''}>Previous</button>
        <button class="secondary-button" id="next-question" ${currentQuiz.index === currentQuiz.questions.length - 1 ? 'disabled' : ''}>Next</button>
        <button class="primary-button" id="finish-quiz">Finish Quiz</button>
      </div>
    </div>`;

  activityContent.querySelectorAll('.option-button').forEach((button) => {
    button.addEventListener('click', () => {
      currentQuiz.answers[question.id] = Number(button.dataset.answer);
      renderQuizQuestion();
    });
  });

  document.getElementById('prev-question').addEventListener('click', () => {
    if (currentQuiz.index > 0) {
      currentQuiz.index -= 1;
      renderQuizQuestion();
    }
  });

  document.getElementById('next-question').addEventListener('click', () => {
    if (currentQuiz.index < currentQuiz.questions.length - 1) {
      currentQuiz.index += 1;
      renderQuizQuestion();
    }
  });

  document.getElementById('finish-quiz').addEventListener('click', async () => {
    const answers = Object.entries(currentQuiz.answers).map(([id, selected]) => ({ id: Number(id), selected }));
    const difficulty = difficultySelect.value;
    const result = await api('/api/quiz', 'POST', { userId: currentUser.id, answers, difficulty });
    if (!result.success) {
      activityContent.innerHTML = `<p class="message">${result.message || 'Unable to submit quiz.'}</p>`;
      return;
    }
    activityContent.innerHTML = `
      <div class="result-box">
        <h3>Quiz complete</h3>
        <p>Score: <strong>${result.score}%</strong></p>
        <p>Correct: <strong>${result.correct}</strong> / ${result.attempted}</p>
        <p>Wrong: <strong>${result.wrong}</strong></p>
      </div>`;
  });
}

async function loadAdminQuestions() {
  const result = await api('/api/admin/questions');
  if (!result.success) {
    questionTableWrapper.innerHTML = `<p class="message">${result.message || 'Unable to load questions.'}</p>`;
    return;
  }

  const rows = result.questions
    .map((question) => `
      <tr>
        <td>${question.id}</td>
        <td>${question.question}</td>
        <td>${question.difficulty}</td>
        <td>Option ${question.correctOption}</td>
        <td>${question.option1}</td>
        <td>${question.option2}</td>
        <td>${question.option3}</td>
        <td>${question.option4}</td>
        <td>
          <button class="secondary-button edit-question-button" data-id="${question.id}">Edit</button>
          <button class="secondary-button delete-question-button" data-id="${question.id}" style="margin-left:8px; background:rgba(239,68,68,0.16); color:#ef4444;">Delete</button>
        </td>
      </tr>`)
    .join('');

  questionTableWrapper.innerHTML = `
    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>Question</th>
          <th>Difficulty</th>
          <th>Answer</th>
          <th>Opt 1</th>
          <th>Opt 2</th>
          <th>Opt 3</th>
          <th>Opt 4</th>
          <th></th>
        </tr>
      </thead>
      <tbody>${rows}</tbody>
    </table>`;

  questionTableWrapper.querySelectorAll('.edit-question-button').forEach((button) => {
    button.addEventListener('click', () => {
      const id = Number(button.dataset.id);
      const question = result.questions.find((item) => item.id === id);
      if (!question) return;
      editingQuestionId = id;
      document.getElementById('question-text').value = question.question;
      document.getElementById('opt1').value = question.option1;
      document.getElementById('opt2').value = question.option2;
      document.getElementById('opt3').value = question.option3;
      document.getElementById('opt4').value = question.option4;
      document.getElementById('correct-option').value = question.correctOption;
      document.getElementById('question-difficulty').value = question.difficulty;
      questionSubmit.textContent = 'Update Question';
      questionCancel.classList.remove('hidden');
    });
  });

  questionTableWrapper.querySelectorAll('.delete-question-button').forEach((button) => {
    button.addEventListener('click', async () => {
      const id = Number(button.dataset.id);
      if (confirm('Are you sure you want to delete this question?')) {
        const result = await api(`/api/admin/questions/${id}`, 'DELETE');
        questionMessage.textContent = result.message || 'Unable to delete question.';
        if (result.success) {
          loadAdminQuestions();
        }
      }
    });
  });
}

questionForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  const question = document.getElementById('question-text').value.trim();
  const option1 = document.getElementById('opt1').value.trim();
  const option2 = document.getElementById('opt2').value.trim();
  const option3 = document.getElementById('opt3').value.trim();
  const option4 = document.getElementById('opt4').value.trim();
  const correctOption = document.getElementById('correct-option').value;
  const difficulty = document.getElementById('question-difficulty').value;

  if (!question || !option1 || !option2 || !option3 || !option4) {
    questionMessage.textContent = 'Please complete all fields.';
    return;
  }

  const payload = { question, option1, option2, option3, option4, correctOption, difficulty };
  const endpoint = editingQuestionId ? `/api/admin/questions/${editingQuestionId}` : '/api/admin/questions';
  const method = editingQuestionId ? 'PUT' : 'POST';
  const result = await api(endpoint, method, payload);

  questionMessage.textContent = result.message || 'Unable to save question.';
  if (result.success) {
    editingQuestionId = null;
    questionForm.reset();
    questionSubmit.textContent = 'Save Question';
    questionCancel.classList.add('hidden');
    loadAdminQuestions();
  }
});

async function showHistory() {
  historyPanel.classList.remove('hidden');
  historyContent.innerHTML = '<p class="message">Loading quiz history...</p>';

  const result = await api(`/api/results?userId=${currentUser.id}`);
  if (!result.success) {
    historyContent.innerHTML = `<p class="message">${result.message || 'Unable to load history.'}</p>`;
    return;
  }

  if (!result.results.length) {
    historyContent.innerHTML = '<p class="message">No quiz history found.</p>';
    return;
  }

  const rows = result.results
    .map((res) => `
      <tr>
        <td>${new Date(res.date).toLocaleDateString()}</td>
        <td>${res.score}%</td>
        <td>${res.correct}/${res.attempted}</td>
        <td>${res.wrong}</td>
      </tr>`)
    .join('');

  historyContent.innerHTML = `
    <table>
      <thead>
        <tr>
          <th>Date</th>
          <th>Score</th>
          <th>Correct/Attempted</th>
          <th>Wrong</th>
        </tr>
      </thead>
      <tbody>${rows}</tbody>
    </table>`;
}
