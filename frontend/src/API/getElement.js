export default function getElement(userEmail, elementId) {
  return fetch(
    'http://localhost:3000/acs/elements/2020b.morb2/' +
      userEmail +
      '/2020b.morb2/' +
      elementId,
    {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    }
  );
}
