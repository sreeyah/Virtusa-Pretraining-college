const express = require('express');
const mysql = require('mysql2/promise');
const crypto = require('crypto');
const path = require('path');

const app = express();
const port = process.env.PORT || 3002;

app.use(express.json());
app.use(express.static(path.join(__dirname, 'public')));

const pool = mysql.createPool({
  host: 'localhost',
  user: 'root',
  password: '1310',
  database: 'quiz_system',
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0
});

function hashPassword(password) {
  return crypto.createHash('sha256').update(password).digest('hex');
}

app.post('/api/login', async (req, res) => {
  const { username, password } = req.body;
  if (!username || !password) return res.status(400).json({ success: false, message: 'Username and password are required.' });

  try {
    const [rows] = await pool.execute(
      'SELECT id, username, role FROM users WHERE username = ? AND password = ?',
      [username, hashPassword(password)]
    );
    if (rows.length === 0) return res.status(401).json({ success: false, message: 'Invalid credentials.' });
    res.json({ success: true, user: rows[0] });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Login failed.' });
  }
});

app.post('/api/register', async (req, res) => {
  const { username, password } = req.body;
  if (!username || !password) return res.status(400).json({ success: false, message: 'Username and password are required.' });

  try {
    await pool.execute(
      'INSERT INTO users(username, password, role) VALUES (?, ?, ?)',
      [username, hashPassword(password), 'user']
    );
    res.json({ success: true, message: 'Registration successful. Please log in.' });
  } catch (error) {
    if (error.code === 'ER_DUP_ENTRY') {
      return res.status(409).json({ success: false, message: 'Username already exists.' });
    }
    console.error(error);
    res.status(500).json({ success: false, message: 'Registration failed.' });
  }
});

app.get('/api/questions', async (req, res) => {
  const difficulty = req.query.difficulty;
  if (!difficulty) return res.status(400).json({ success: false, message: 'Difficulty is required.' });

  try {
    const [rows] = await pool.execute(
      'SELECT id, question_text AS question, option1, option2, option3, option4, correct_option AS correctOption, difficulty FROM questions WHERE difficulty = ?',
      [difficulty]
    );
    res.json({ success: true, questions: rows });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Could not load questions.' });
  }
});

app.post('/api/quiz', async (req, res) => {
  const { userId, answers, difficulty } = req.body;
  if (!userId || !answers || !difficulty) return res.status(400).json({ success: false, message: 'userId, answers and difficulty are required.' });

  try {
    const [questions] = await pool.execute(
      'SELECT id, correct_option AS correctOption FROM questions WHERE difficulty = ?',
      [difficulty]
    );
    const questionMap = new Map(questions.map(q => [q.id, q.correctOption]));
    let correct = 0;
    let attempted = 0;

    answers.forEach(answer => {
      const correctOption = questionMap.get(answer.id);
      if (correctOption) {
        attempted += 1;
        if (answer.selected === correctOption) correct += 1;
      }
    });

    const wrong = Math.max(0, attempted - correct);
    const score = Math.round((correct / Math.max(attempted, 1)) * 100);

    await pool.execute(
      'INSERT INTO quiz_results(user_id, score, attempted, correct, wrong) VALUES (?, ?, ?, ?, ?)',
      [userId, score, attempted, correct, wrong]
    );

    res.json({ success: true, score, attempted, correct, wrong });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Could not submit quiz.' });
  }
});

app.get('/api/results', async (req, res) => {
  const userId = req.query.userId;
  if (!userId) return res.status(400).json({ success: false, message: 'userId is required.' });

  try {
    const [rows] = await pool.execute(
      'SELECT quiz_date AS date, score, attempted, correct, wrong FROM quiz_results WHERE user_id = ? ORDER BY quiz_date DESC LIMIT 10',
      [userId]
    );
    res.json({ success: true, results: rows });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Could not load results.' });
  }
});

app.get('/api/admin/questions', async (req, res) => {
  try {
    const [rows] = await pool.execute(
      'SELECT id, question_text AS question, option1, option2, option3, option4, correct_option AS correctOption, difficulty FROM questions ORDER BY difficulty, id'
    );
    res.json({ success: true, questions: rows });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Could not load admin questions.' });
  }
});

app.post('/api/admin/questions', async (req, res) => {
  const { question, option1, option2, option3, option4, correctOption, difficulty } = req.body;
  if (!question || !option1 || !option2 || !option3 || !option4 || !correctOption || !difficulty) {
    return res.status(400).json({ success: false, message: 'All fields are required.' });
  }

  try {
    await pool.execute(
      'INSERT INTO questions(question_text, option1, option2, option3, option4, correct_option, difficulty) VALUES (?, ?, ?, ?, ?, ?, ?)',
      [question, option1, option2, option3, option4, correctOption, difficulty]
    );
    res.json({ success: true, message: 'Question added.' });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Could not add question.' });
  }
});

app.put('/api/admin/questions/:id', async (req, res) => {
  const { id } = req.params;
  const { question, option1, option2, option3, option4, correctOption, difficulty } = req.body;
  if (!question || !option1 || !option2 || !option3 || !option4 || !correctOption || !difficulty) {
    return res.status(400).json({ success: false, message: 'All fields are required.' });
  }

  try {
    await pool.execute(
      'UPDATE questions SET question_text = ?, option1 = ?, option2 = ?, option3 = ?, option4 = ?, correct_option = ?, difficulty = ? WHERE id = ?',
      [question, option1, option2, option3, option4, correctOption, difficulty, id]
    );
    res.json({ success: true, message: 'Question updated.' });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Could not update question.' });
  }
});

app.delete('/api/admin/questions/:id', async (req, res) => {
  const { id } = req.params;

  try {
    await pool.execute('DELETE FROM questions WHERE id = ?', [id]);
    res.json({ success: true, message: 'Question deleted.' });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Could not delete question.' });
  }
});

async function ensureAdminUser() {
  try {
    const [rows] = await pool.execute('SELECT id FROM users WHERE role = ? LIMIT 1', ['admin']);
    if (rows.length === 0) {
      await pool.execute(
        'INSERT INTO users(username, password, role) VALUES (?, ?, ?)',
        ['admin', hashPassword('admin123'), 'admin']
      );
      console.log('Created default admin credentials: admin / admin123');
    }
  } catch (error) {
    console.error('Failed to ensure admin user:', error);
  }
}

ensureAdminUser().then(() => {
  app.listen(port, () => {
    console.log(`Quiz web server running on http://localhost:${port}`);
  });
});
