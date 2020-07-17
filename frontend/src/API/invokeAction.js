export default function invokeAction(type, elementId, userEmail, att) {
  return fetch('http://localhost:3000/acs/actions', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      type: type,
      element: {
        elementId: {
          domain: '2020b.morb2',
          id: elementId,
        },
      },
      invokedBy: {
        userId: {
          domain: '2020b.morb2',
          email: userEmail,
        },
      },
      actionAttributes: {
        ...att,
      },
    }),
  });
}
