# Enhance AI Assistant Prompt & Formatting

Refine the backend prompt for the OpenAI/OpenRouter integration to ensure cleaner, more human-readable responses that align with the Expense Tracker's minimalist Black & White theme.

## User Review Required

> [!IMPORTANT]
> The AI will be strictly forbidden from mentioning technical data like MongoDB `_id`s or using Markdown formatting like `*` for bolding, which clutters the UI.

## Proposed Changes

### Backend (Node.js)

#### [MODIFY] [generateAIChatResponse Function]
- **Remove Technical Data**: Add a rule to strip out technical identifiers (IDs, versions, etc.) from the conversation.
- **Plain Text Formatting**: Replace asterisks (`*`) with simple dashes (`-`) for lists and forbid bolding symbols to ensure the text looks clean in your B&W UI.
- **Robot Persona**: Align the response style with the "Robot Assistant" persona established in the Android UI.
- **Strict Accuracy**: Reinforce that the AI should only use the specific numbers provided in the `analytics` and `transactions` JSON.

## Verification Plan

### Manual Verification
1. **No IDs**: Ask the AI "What was my last transaction?". Verify it says "₹50 for Coffee" instead of mentioning `ObjectId(...)`.
2. **Clean Text**: Verify the response doesn't contain `*` or `**` symbols.
3. **Accuracy**: Compare a response about "Total Balance" with the actual number shown on the Home Screen.
